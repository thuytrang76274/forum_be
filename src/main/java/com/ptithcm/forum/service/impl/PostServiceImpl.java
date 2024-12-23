package com.ptithcm.forum.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptithcm.forum.dto.IssueDto;
import com.ptithcm.forum.dto.PageDto;
import com.ptithcm.forum.dto.PostDto;
import com.ptithcm.forum.dto.PostViewRequest;
import com.ptithcm.forum.dto.ResponseDto;
import com.ptithcm.forum.entity.Issue;
import com.ptithcm.forum.entity.IssueHistory;
import com.ptithcm.forum.entity.IssueStatus;
import com.ptithcm.forum.entity.Post;
import com.ptithcm.forum.entity.PostStatus;
import com.ptithcm.forum.entity.SystemCodeDetail;
import com.ptithcm.forum.entity.User;
import com.ptithcm.forum.exception.NotAllowedException;
import com.ptithcm.forum.exception.NotFoundException;
import com.ptithcm.forum.mapping.IssueMapping;
import com.ptithcm.forum.mapping.PostMapping;
import com.ptithcm.forum.repository.CommentRepository;
import com.ptithcm.forum.repository.IssueHistoryRepository;
import com.ptithcm.forum.repository.IssueRepository;
import com.ptithcm.forum.repository.PostRepository;
import com.ptithcm.forum.repository.SystemCodeDetailRepository;
import com.ptithcm.forum.service.CommonService;
import com.ptithcm.forum.service.PostService;
import com.ptithcm.forum.util.StringConstants;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PostServiceImpl implements PostService {

  private final PostRepository postRepository;
  private final SystemCodeDetailRepository systemCodeDetailRepository;
  private final IssueRepository issueRepository;
  private final IssueHistoryRepository issueHistoryRepository;
  private final CommentRepository commentRepository;
  private final PostMapping postMapping;
  private final IssueMapping issueMapping;
  private final ObjectMapper objectMapper;
  private final CommonService commonService;

  public PostServiceImpl(PostRepository postRepository,
      SystemCodeDetailRepository systemCodeDetailRepository, IssueRepository issueRepository,
      IssueHistoryRepository issueHistoryRepository, CommentRepository commentRepository,
      PostMapping postMapping, IssueMapping issueMapping, ObjectMapper objectMapper,
      CommonService commonService) {
    this.postRepository = postRepository;
    this.systemCodeDetailRepository = systemCodeDetailRepository;
    this.issueRepository = issueRepository;
    this.issueHistoryRepository = issueHistoryRepository;
    this.commentRepository = commentRepository;
    this.postMapping = postMapping;
    this.issueMapping = issueMapping;
    this.objectMapper = objectMapper;
    this.commonService = commonService;
  }

  @Override
  public ResponseDto<PageDto<PostDto>> getPageOfPosts(PostViewRequest postViewRequest) {
    if (isDateTypeNotAllowed(postViewRequest)) {
      throw new NotAllowedException(commonService.getMessage(StringConstants.USER_NOT_ALLOWED));
    }
    if (isFromDateAndToDateNotValid(postViewRequest)) {
      throw new NotAllowedException(
          commonService.getMessage(StringConstants.FROM_DATE_TO_DATE_ERROR));
    }
    if (postViewRequest.getPage() < 0) {
      postViewRequest.setPage(0);
    }
    if (postViewRequest.getPage() < 0) {
      postViewRequest.setPage(10);
    }
    Pageable pageable = PageRequest.of(postViewRequest.getPage(), postViewRequest.getSize());
    Page<Post> postPage = postRepository.getPostsByConditions(postViewRequest, pageable);
    return new ResponseDto<>(PageDto.<PostDto>builder()
        .page(postViewRequest.getPage())
        .size(postViewRequest.getSize())
        .totalElements(postPage.getTotalElements())
        .totalPages(postPage.getTotalPages())
        .hasPrevious(postPage.hasPrevious())
        .hasNext(postPage.hasNext())
        .data(postPage.getContent().stream().map(postMapping::convertPostToPostDto)
            .collect(
                Collectors.toList()))
        .build());
  }

  @Override
  @Transactional
  public ResponseDto<PostDto> createPost(PostDto postCreateDto) {
    User currentUser = commonService.getCurrentUserLoggedIn();
    Post post = postMapping.convertPostDtoToPost(postCreateDto);
    Issue issue = getIssueById(postCreateDto.getIssue().getId());
    if (!isCurrentUserAllowed(issue, currentUser)) {
      throw new NotAllowedException(commonService.getMessage(StringConstants.USER_NOT_ALLOWED));
    }
    post.setIssue(issue);
    SystemCodeDetail module = getModuleById(postCreateDto.getModule().getId());
    post.setModule(module);
    if (isChangeCustomer(postCreateDto, issue)) {
      SystemCodeDetail customer = getModuleById(postCreateDto.getCustomer().getId());
      issue.setCustomer(customer);
    }
    post.setStatus(PostStatus.WAITING_APPROVE);
    LocalDateTime now = LocalDateTime.now();
    post.setCreatedAt(now);
    post.setModifiedAt(now);
    post.setCreatedBy(currentUser.getUsername());
    post.setModifiedBy(currentUser.getUsername());
    Post savedPost = postRepository.save(post);
    changeStatusOfIssueWhenPostCreated(issue, currentUser);
    return new ResponseDto<>(postMapping.convertPostToPostDto(savedPost));
  }

  @Override
  public ResponseDto<PostDto> getSinglePost(Long postId) {
    return new ResponseDto<>(postMapping.convertPostToPostDtoWithComments(
        postRepository.findPostDetailById(postId).orElseThrow(
            () -> new NotFoundException(commonService.getMessage(StringConstants.POST_NOT_FOUND)))
    ));
  }

  @Override
  public ResponseDto<List<PostDto>> getPostsByIssueId(Long issueId) {
    getIssueById(issueId);
    return new ResponseDto<>(postRepository.getListPostDetailByIssueId(issueId)
        .stream().map(postMapping::convertPostToPostDto).toList());
  }

  @Override
  public void deletePost(Long postId) {
    User currentUser = commonService.getCurrentUserLoggedIn();
    Post post = getPostById(postId);
    if (post.isAnalyst()) {
      changeStatusOfIssueWhenAnalystPostIsDeleted(post.getIssue(), currentUser);
    }
    postRepository.delete(post);
  }

  @Override
  @Transactional
  public ResponseDto<PostDto> updatePost(Long postId, PostDto postUpdateDto) {
    Post post = getPostById(postId);
    User currentUser = commonService.getCurrentUserLoggedIn();
    Issue issue = post.getIssue();
    if (!isCurrentUserAllowed(issue, currentUser)) {
      throw new NotAllowedException(commonService.getMessage(StringConstants.USER_NOT_ALLOWED));
    }
    updateFields(postUpdateDto, post);
    updateStatus(postUpdateDto, post, currentUser);
    LocalDateTime now = LocalDateTime.now();
    post.setModifiedAt(now);
    post.setModifiedBy(currentUser.getUsername());
    Post updatedPost = postRepository.save(post);
    return new ResponseDto<>(postMapping.convertPostToPostDto(updatedPost));
  }

  private void changeStatusOfIssueWhenPostCreated(Issue currentIssue, User currentUser) {
    if (currentIssue.isNew() || currentIssue.isAssigned()) {
      storeToHistory(currentIssue, currentUser);
      currentIssue.setStatus(IssueStatus.DISCUSSING);
      issueRepository.save(currentIssue);
      return;
    }
    throw new NotAllowedException(commonService.getMessage(StringConstants.USER_NOT_ALLOWED));
  }

  private void changeStatusOfIssueWhenPostUpdatedToAnalyst(Issue currentIssue, User currentUser) {
    if (currentIssue.isDiscussing()) {
      storeToHistory(currentIssue, currentUser);
      currentIssue.setStatus(IssueStatus.ANALYST);
      issueRepository.save(currentIssue);
      return;
    }
    throw new NotAllowedException(commonService.getMessage(StringConstants.USER_NOT_ALLOWED));
  }

  private void changeStatusOfIssueWhenPostUpdatedToDone(Issue currentIssue, User currentUser) {
    if (currentIssue.isAnalyst()) {
      storeToHistory(currentIssue, currentUser);
      currentIssue.setStatus(IssueStatus.DONE);
      issueRepository.save(currentIssue);
      return;
    }
    throw new NotAllowedException(commonService.getMessage(StringConstants.USER_NOT_ALLOWED));
  }

  private void changeStatusOfIssueWhenPostUpdatedToPendingOrClose(Issue currentIssue,
      User currentUser,
      String newStatus) {
    if (currentIssue.isNew() || currentIssue.isAssigned() || currentIssue.isAnalyst()
        || currentIssue.isDiscussing() || currentIssue.isDone()) {
      storeToHistory(currentIssue, currentUser);
      currentIssue.setStatus(IssueStatus.valueOf(newStatus));
      issueRepository.save(currentIssue);
      return;
    }
    throw new NotAllowedException(commonService.getMessage(StringConstants.USER_NOT_ALLOWED));
  }

  private void changeStatusOfIssueWhenAnalystPostIsDeleted(Issue issue, User currentUser) {
    if (issue.isDone() || issue.isAnalyst()) {
      storeToHistory(issue, currentUser);
      issue.setStatus(IssueStatus.ASSIGNED);
      issueRepository.save(issue);
      return;
    }
    throw new NotAllowedException(commonService.getMessage(StringConstants.USER_NOT_ALLOWED));
  }

  private void updateStatus(PostDto postUpdateDto, Post currentPost, User currentUser) {
    if (null == postUpdateDto.getStatus()) {
      return;
    }
    switch (currentPost.getStatus()) {
      case WAITING_APPROVE -> changeFromWaitingApprove(postUpdateDto, currentPost, currentUser);
      case ANALYST -> changeFromAnalyst(postUpdateDto, currentPost, currentUser);
      case DONE -> changeFromDone(postUpdateDto, currentPost, currentUser);
      case PENDING -> changeFromPending(postUpdateDto, currentPost, currentUser);
      case CLOSE -> changeFromClose(postUpdateDto, currentPost, currentUser);
    }
  }

  private void changeFromWaitingApprove(PostDto postUpdateDto, Post currentPost, User currentUser) {
    if (postUpdateDto.isAnalyst()) {
      currentPost.setStatus(postUpdateDto.getStatus());
      currentPost.setApprovedAt(LocalDateTime.now());
      changeStatusOfIssueWhenPostUpdatedToAnalyst(currentPost.getIssue(), currentUser);
      return;
    }
    if (postUpdateDto.isPending() || postUpdateDto.isClose()) {
      currentPost.setStatus(postUpdateDto.getStatus());
      changeStatusOfIssueWhenPostUpdatedToPendingOrClose(currentPost.getIssue(), currentUser,
          postUpdateDto.getStatus().toString());
      return;
    }
    throw new NotAllowedException(commonService.getMessage(StringConstants.USER_NOT_ALLOWED));
  }

  private void changeFromAnalyst(PostDto postUpdateDto, Post currentPost, User currentUser) {
    if (postUpdateDto.isDone() && isPostHasAnySolutions(currentPost)) {
      currentPost.setStatus(postUpdateDto.getStatus());
      changeStatusOfIssueWhenPostUpdatedToDone(currentPost.getIssue(), currentUser);
      return;
    }
    if (postUpdateDto.isPending() || postUpdateDto.isClose()) {
      currentPost.setStatus(postUpdateDto.getStatus());
      changeStatusOfIssueWhenPostUpdatedToPendingOrClose(currentPost.getIssue(), currentUser,
          postUpdateDto.getStatus().toString());
      return;
    }
    throw new NotAllowedException(commonService.getMessage(StringConstants.USER_NOT_ALLOWED));
  }

  private void changeFromDone(PostDto postUpdateDto, Post currentPost, User currentUser) {
    if (postUpdateDto.isAnalyst()) {
      currentPost.setStatus(postUpdateDto.getStatus());
      changeStatusOfIssueWhenPostUpdatedToAnalyst(currentPost.getIssue(), currentUser);
      return;
    }
    if (postUpdateDto.isClose()) {
      currentPost.setStatus(postUpdateDto.getStatus());
      changeStatusOfIssueWhenPostUpdatedToPendingOrClose(currentPost.getIssue(), currentUser,
          postUpdateDto.getStatus().toString());
      return;
    }
    throw new NotAllowedException(commonService.getMessage(StringConstants.USER_NOT_ALLOWED));
  }

  private void changeFromPending(PostDto postUpdateDto, Post currentPost, User currentUser) {
    if (postUpdateDto.isAnalyst()) {
      currentPost.setStatus(postUpdateDto.getStatus());
      changeStatusOfIssueWhenPostUpdatedToAnalyst(currentPost.getIssue(), currentUser);
      return;
    }
    if (postUpdateDto.isWaitingApprove()) {
      currentPost.setStatus(postUpdateDto.getStatus());
      changeStatusOfIssueWhenPostCreated(currentPost.getIssue(), currentUser);
      return;
    }
    if (postUpdateDto.isDone() && isPostHasAnySolutions(currentPost)) {
      currentPost.setStatus(postUpdateDto.getStatus());
      changeStatusOfIssueWhenPostUpdatedToPendingOrClose(currentPost.getIssue(), currentUser,
          postUpdateDto.getStatus().toString());
      return;
    }
    throw new NotAllowedException(commonService.getMessage(StringConstants.USER_NOT_ALLOWED));
  }

  private void changeFromClose(PostDto postUpdateDto, Post currentPost, User currentUser) {
    if (postUpdateDto.isAnalyst()) {
      currentPost.setStatus(postUpdateDto.getStatus());
      changeStatusOfIssueWhenPostUpdatedToAnalyst(currentPost.getIssue(), currentUser);
      return;
    }
    if (postUpdateDto.isDone() && isPostHasAnySolutions(currentPost)) {
      currentPost.setStatus(postUpdateDto.getStatus());
      changeStatusOfIssueWhenPostUpdatedToDone(currentPost.getIssue(), currentUser);
      return;
    }
    if (postUpdateDto.isPending()) {
      currentPost.setStatus(postUpdateDto.getStatus());
      changeStatusOfIssueWhenPostUpdatedToPendingOrClose(currentPost.getIssue(), currentUser,
          postUpdateDto.getStatus().toString());
      return;
    }
    throw new NotAllowedException(commonService.getMessage(StringConstants.USER_NOT_ALLOWED));
  }

  @SneakyThrows
  private void storeToHistory(Issue issue, User currentUser) {
    IssueDto issueDto = issueMapping.convertIssueToIssueDto(issue);
    String issueOld = objectMapper.writeValueAsString(issueDto);
    IssueHistory issueHistory = new IssueHistory();
    issueHistory.setIssue(issue);
    issueHistory.setContent(issueOld);
    issueHistory.setModifiedBy(currentUser.getUsername());
    issueHistory.setModifiedAt(LocalDateTime.now());
    IssueHistory newIssueHistory = issueHistoryRepository.save(issueHistory);
    issue.addHistory(newIssueHistory);
  }

  private void updateFields(PostDto postUpdateDto, Post post) {
    post.setTitle(
        StringUtils.isBlank(postUpdateDto.getTitle()) ? post.getTitle() : postUpdateDto.getTitle());

    post.setDescription(StringUtils.isBlank(postUpdateDto.getDescription()) ? post.getDescription()
        : postUpdateDto.getDescription());

    post.setApplyFor(
        null == postUpdateDto.getApplyFor() ? post.getApplyFor() : postUpdateDto.getApplyFor());
    post.setModule(null == postUpdateDto.getModule() ? post.getModule()
        : getModuleById(postUpdateDto.getModule().getId()));
  }

  private boolean isCurrentUserAllowed(Issue currentIssue, User currentUser) {
    return currentUser.isAdmin() || isCurrentUserLoggedInNotAdminAndOneOfAssignees(currentIssue,
        currentUser);
  }

  private boolean isCurrentUserLoggedInNotAdminAndOneOfAssignees(Issue currentIssue,
      User currentUserLoggedIn) {
    return currentUserLoggedIn.isUser() && commonService.isCurrentUserLoggedInOneOfUsers(
        currentIssue.getAssignees(), currentUserLoggedIn);
  }

  private boolean isChangeCustomer(PostDto postCreateDto, Issue issue) {
    if (postCreateDto.getCustomer() == null) {
      return false;
    }
    if (null == issue.getCustomer()) {
      return false;
    }
    return postCreateDto.getCustomer().getId().equals(issue.getCustomer().getId());
  }

  private boolean isPostHasAnySolutions(Post post) {
    return !commentRepository.getSolutionCommentsByPostId(post.getId()).isEmpty();
  }

  private boolean isDateTypeNotAllowed(PostViewRequest postViewRequest) {
    return StringConstants.POST_DATE_TYPE_ACCEPT.stream()
        .noneMatch(d -> d.equalsIgnoreCase(postViewRequest.getTypeDate()));
  }

  private boolean isFromDateAndToDateNotValid(PostViewRequest postViewRequest) {
    return postViewRequest.getFromDate() != null && postViewRequest.getToDate() != null &&
        postViewRequest.getToDate().isBefore(postViewRequest.getFromDate());
  }

  private Post getPostById(Long postId) {
    return postRepository.findById(postId).orElseThrow(
        () -> new NotFoundException(commonService.getMessage(StringConstants.POST_NOT_FOUND))
    );
  }

  private Issue getIssueById(Long issueId) {
    return issueRepository.findById(issueId).orElseThrow(
        () -> new NotFoundException(commonService.getMessage(StringConstants.ISSUE_NOT_FOUND))
    );
  }

  private SystemCodeDetail getModuleById(Long moduleId) {
    return systemCodeDetailRepository.findById(moduleId).orElseThrow(
        () -> new NotFoundException(commonService.getMessage(StringConstants.MODULE_NOT_FOUND))
    );
  }
}
