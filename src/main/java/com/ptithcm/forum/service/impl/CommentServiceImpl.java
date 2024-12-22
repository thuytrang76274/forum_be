package com.ptithcm.forum.service.impl;

import com.ptithcm.forum.dto.CommentDto;
import com.ptithcm.forum.dto.ResponseDto;
import com.ptithcm.forum.entity.Comment;
import com.ptithcm.forum.entity.Issue;
import com.ptithcm.forum.entity.Post;
import com.ptithcm.forum.entity.User;
import com.ptithcm.forum.exception.NotAllowedException;
import com.ptithcm.forum.exception.NotFoundException;
import com.ptithcm.forum.mapping.CommentMapping;
import com.ptithcm.forum.repository.CommentRepository;
import com.ptithcm.forum.repository.PostRepository;
import com.ptithcm.forum.service.CommentService;
import com.ptithcm.forum.service.CommonService;
import com.ptithcm.forum.util.StringConstants;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {

  private final CommentRepository commentRepository;
  private final PostRepository postRepository;
  private final CommentMapping commentMapping;
  private final CommonService commonService;

  public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository,
      CommentMapping commentMapping,
      CommonService commonService) {
    this.commentRepository = commentRepository;
    this.postRepository = postRepository;
    this.commentMapping = commentMapping;
    this.commonService = commonService;
  }

  @Override
  public ResponseDto<CommentDto> getSingleComment(Long commentId) {
    return new ResponseDto<>(commentMapping.convertCommentToCommentDto(getSingleById(commentId)));
  }

  @Override
  public ResponseDto<CommentDto> createComment(CommentDto commentDto) {
    Post post = postRepository.findById(commentDto.getPost().getId()).orElseThrow(
        () -> new NotFoundException(commonService.getMessage(StringConstants.POST_NOT_FOUND)));
    if (!post.isAnalyst()) {
      throw new NotAllowedException(commonService.getMessage(StringConstants.USER_NOT_ALLOWED));
    }
    Comment comment = new Comment();
    comment.setContent(commentDto.getContent());
    comment.setPost(post);
    User currentUser = commonService.getCurrentUserLoggedIn();
    LocalDateTime now = LocalDateTime.now();
    comment.setCreatedAt(now);
    comment.setModifiedAt(now);
    comment.setCreatedBy(currentUser.getUsername());
    comment.setModifiedBy(currentUser.getUsername());
    Comment savedComment = commentRepository.save(comment);
    return new ResponseDto<>(commentMapping.convertCommentToCommentDto(savedComment));
  }

  @Override
  public ResponseDto<CommentDto> updateComment(Long commentId, CommentDto commentUpdateDto) {
    Comment comment = getSingleById(commentId);
    if (comment.getIsSolution()) {
      throw new NotAllowedException(commonService.getMessage(StringConstants.USER_NOT_ALLOWED));
    }
    User currentUserLoggedIn = commonService.getCurrentUserLoggedIn();
    if (!isCurrentUserLoggedInWantToUpdate(currentUserLoggedIn, comment)) {
      throw new NotAllowedException(commonService.getMessage(StringConstants.USER_NOT_ALLOWED));
    }
    comment.setContent(null == commentUpdateDto.getContent() ? comment.getContent()
        : commentUpdateDto.getContent());
    comment.setIsSolution(null == commentUpdateDto.getIsSolution() ? comment.getIsSolution()
        : commentUpdateDto.getIsSolution());
    LocalDateTime now = LocalDateTime.now();
    comment.setModifiedAt(now);
    comment.setModifiedBy(currentUserLoggedIn.getUsername());
    Comment savedComment = commentRepository.save(comment);
    return new ResponseDto<>(commentMapping.convertCommentToCommentDto(savedComment));
  }

  @Override
  @Transactional
  public ResponseDto<CommentDto> pinComment(Long commentId) {
    Comment currentComment = getSingleById(commentId);
    if (currentComment.getIsSolution()) {
      throw new NotAllowedException(commonService.getMessage(StringConstants.USER_NOT_ALLOWED));
    }
    validateCurrentLoggedInAllowedToDoThisAction(currentComment);
    currentComment.setIsSolution(true);
    Comment savedComment = commentRepository.save(currentComment);
    return new ResponseDto<>(commentMapping.convertCommentToCommentDto(savedComment));
  }

  @Override
  @Transactional
  public ResponseDto<CommentDto> unpinComment(Long commentId) {
    Comment currentComment = getSingleById(commentId);
    if (!currentComment.getIsSolution()) {
      throw new NotAllowedException(commonService.getMessage(StringConstants.USER_NOT_ALLOWED));
    }
    validateCurrentLoggedInAllowedToDoThisAction(currentComment);
    currentComment.setIsSolution(false);
    Comment savedComment = commentRepository.save(currentComment);
    return new ResponseDto<>(commentMapping.convertCommentToCommentDto(savedComment));
  }

  @Override
  public ResponseDto<List<CommentDto>> getCommentsByPost(Long postId, boolean isSolution) {
    Post post = getSinglePostById(postId);
    return new ResponseDto<>(
        commentRepository.getCommentsByPostIdWithSolution(post.getId(), isSolution).stream()
            .map(commentMapping::convertCommentToCommentDto)
            .collect(Collectors.toList()));
  }

  @Override
  @Transactional
  public void deleteComment(Long commentId) {
    Comment currentComment = getSingleById(commentId);
    if (currentComment.getIsSolution()) {
      throw new NotAllowedException(commonService.getMessage(StringConstants.USER_NOT_ALLOWED));
    }
    commentRepository.delete(currentComment);
  }

  private Comment getSingleById(Long commentId) {
    return commentRepository.findById(commentId).orElseThrow(
        () -> new NotFoundException(commonService.getMessage(StringConstants.COMMENT_NOT_FOUND)));
  }

  private Post getSinglePostById(Long postId) {
    return postRepository.findById(postId).orElseThrow(() -> new NotFoundException(
        commonService.getMessage(StringConstants.POST_NOT_FOUND)));
  }

  private void validateCurrentLoggedInAllowedToDoThisAction(Comment currentComment) {
    User currentUserLoggedIn = commonService.getCurrentUserLoggedIn();
    if (!isCurrentUserLoggedInAllowedToDoThisAction(currentUserLoggedIn,
        currentComment.getPost().getIssue())) {
      throw new NotAllowedException(commonService.getMessage(StringConstants.USER_NOT_ALLOWED));
    }
  }

  private boolean isCurrentUserLoggedInWantToUpdate(User currentUserLoggedIn, Comment comment) {
    return currentUserLoggedIn.getUsername().equals(comment.getCreatedBy());
  }

  private boolean isCurrentUserLoggedInNotAdminAndOneOfAssignees(User currentUserLoggedIn,
      Issue currentIssue) {
    return currentUserLoggedIn.isUser() && commonService.isCurrentUserLoggedInOneOfUsers(
        currentIssue.getAssignees(), currentUserLoggedIn);
  }

  private boolean isCurrentUserLoggedInAllowedToDoThisAction(User currentUserLoggedIn,
      Issue currentIssue) {
    return currentUserLoggedIn.isAdmin() || isCurrentUserLoggedInNotAdminAndOneOfAssignees(
        currentUserLoggedIn, currentIssue);
  }
}
