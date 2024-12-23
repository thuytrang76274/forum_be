package com.ptithcm.forum.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptithcm.forum.config.MinioConfiguration;
import com.ptithcm.forum.dto.IdDto;
import com.ptithcm.forum.dto.IssueDto;
import com.ptithcm.forum.dto.IssueViewRequest;
import com.ptithcm.forum.dto.PageDto;
import com.ptithcm.forum.dto.PenpotCommentDto;
import com.ptithcm.forum.dto.PenpotFileDto;
import com.ptithcm.forum.dto.PenpotPageDto;
import com.ptithcm.forum.dto.PenpotPageFlowDto;
import com.ptithcm.forum.dto.PenpotTeamDto;
import com.ptithcm.forum.dto.ResponseDto;
import com.ptithcm.forum.dto.UserDto;
import com.ptithcm.forum.entity.Image;
import com.ptithcm.forum.entity.Issue;
import com.ptithcm.forum.entity.IssueHistory;
import com.ptithcm.forum.entity.IssueStatus;
import com.ptithcm.forum.entity.Post;
import com.ptithcm.forum.entity.SystemCodeDetail;
import com.ptithcm.forum.entity.User;
import com.ptithcm.forum.exception.DateTimeException;
import com.ptithcm.forum.exception.NotAllowedException;
import com.ptithcm.forum.exception.NotFoundException;
import com.ptithcm.forum.exception.PenpotException;
import com.ptithcm.forum.mapping.IssueMapping;
import com.ptithcm.forum.repository.ImageRepository;
import com.ptithcm.forum.repository.IssueHistoryRepository;
import com.ptithcm.forum.repository.IssueRepository;
import com.ptithcm.forum.repository.PostRepository;
import com.ptithcm.forum.repository.SystemCodeDetailRepository;
import com.ptithcm.forum.repository.UserRepository;
import com.ptithcm.forum.service.CommonService;
import com.ptithcm.forum.service.IssueService;
import com.ptithcm.forum.util.MinioUtil;
import com.ptithcm.forum.util.StringConstants;
import com.ptithcm.forum.util.TimeUtil;
import io.minio.errors.MinioException;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class IssueServiceImpl implements IssueService {

  @Value("${penpot.api}")
  private String PENPOT_API_URL;
  @Value("${system-code.penpot-token}")
  private String PENPOT_TOKEN;
  @Value("${penpot.get-teams}")
  private String PENPOT_TEAMS;
  @Value("${penpot.get-team-recent-files}")
  private String PENPOT_TEAM_RECENT_FILES;
  @Value("${penpot.get-comment-threads}")
  private String PENPOT_COMMENT_THREADS;
  @Value("${penpot.get-file-object-thumbnails}")
  private String PENPOT_FILE_OBJECT_THUMBNAILS;
  @Value("${penpot.get-assets-by-id}")
  private String PENPOT_ASSETS_BY_ID;
  @Value("${penpot.get-page}")
  private String PENPOT_GET_PAGE;
  @Value("${penpot.app}")
  private String PENPOT_APP_URL;
  @Value("${penpot.prototype-link}")
  private String PENPOT_PROTOTYPE_LINK;
  private final IssueRepository issueRepository;
  private final PostRepository postRepository;
  private final IssueMapping issueMapping;
  private final UserRepository userRepository;
  private final ImageRepository imageRepository;
  private final ObjectMapper objectMapper;
  private final MinioConfiguration minioConfiguration;
  private final MinioUtil minioUtil;
  private final SystemCodeDetailRepository systemCodeDetailRepository;
  private final IssueHistoryRepository issueHistoryRepository;
  private final CommonService commonService;
  private final RestTemplate restTemplate;

  public IssueServiceImpl(IssueRepository issueRepository, PostRepository postRepository,
      IssueMapping issueMapping,
      UserRepository userRepository, ImageRepository imageRepository, ObjectMapper objectMapper,
      MinioConfiguration minioConfiguration, MinioUtil minioUtil,
      SystemCodeDetailRepository systemCodeDetailRepository,
      IssueHistoryRepository issueHistoryRepository, CommonService commonService,
      RestTemplate restTemplate) {
    this.issueRepository = issueRepository;
    this.postRepository = postRepository;
    this.issueMapping = issueMapping;
    this.userRepository = userRepository;
    this.imageRepository = imageRepository;
    this.objectMapper = objectMapper;
    this.minioConfiguration = minioConfiguration;
    this.minioUtil = minioUtil;
    this.systemCodeDetailRepository = systemCodeDetailRepository;
    this.issueHistoryRepository = issueHistoryRepository;
    this.commonService = commonService;
    this.restTemplate = restTemplate;
  }

  @Override
  public ResponseDto<PageDto<IssueDto>> getPageOfIssues(IssueViewRequest issueViewRequest) {
    if (isDateTypeNotAllowed(issueViewRequest)) {
      throw new NotAllowedException(commonService.getMessage(StringConstants.USER_NOT_ALLOWED));
    }
    if (isFromDateAndToDateNotValid(issueViewRequest)) {
      throw new DateTimeException(
          commonService.getMessage(StringConstants.FROM_DATE_TO_DATE_ERROR));
    }
    if (issueViewRequest.getPage() < 0) {
      issueViewRequest.setPage(0);
    }
    if (issueViewRequest.getSize() < 0) {
      issueViewRequest.setSize(10);
    }
    Pageable pageable = PageRequest.of(issueViewRequest.getPage(), issueViewRequest.getSize());
    Page<Issue> issuePage = issueRepository.getIssuesByConditions(issueViewRequest, pageable);
    return new ResponseDto<>(PageDto.<IssueDto>builder()
        .page(issueViewRequest.getPage())
        .size(issueViewRequest.getSize())
        .totalElements(issuePage.getTotalElements())
        .totalPages(issuePage.getTotalPages())
        .hasPrevious(issuePage.hasPrevious())
        .hasNext(issuePage.hasNext())
        .data(
            issuePage.getContent().stream().map(issueMapping::convertIssueToIssueDto)
                .collect(Collectors.toList()))
        .build());
  }

  @Override
  public ResponseDto<List<String>> getVersions() {
    return new ResponseDto<>(issueRepository.getAllVersions().stream().map(Issue::getVersion)
        .collect(Collectors.toList()));
  }

  @Override
  @Transactional
  @SneakyThrows
  public ResponseDto<IssueDto> createIssue(MultipartFile imageFile, String issueString) {
    User currentUserLoggedIn = commonService.getCurrentUserLoggedIn();
    if (currentUserLoggedIn.isUser()) {
      throw new NotAllowedException(commonService.getMessage(StringConstants.USER_NOT_ALLOWED));
    }
    IssueDto issueCreateDto = objectMapper.readValue(issueString, IssueDto.class);
    if (TimeUtil.isBeforeDateNow(issueCreateDto.getDueDate())) {
      throw new DateTimeException(
          commonService.getMessage(StringConstants.DUE_DATE_NOT_ACCEPTABLE));
    }
    Issue issue = issueMapping.convertIssueDtoToIssue(issueCreateDto);
    updateIssueType(issueCreateDto.getType().getId(), issue);
    updateCustomer(issueCreateDto.getCustomer().getId(), issue);
    issue.setStatus(IssueStatus.NEW);
    if (!issueCreateDto.isAssigneeEmpty()) {
      issue.setStatus(IssueStatus.ASSIGNED);
      updateAssignees(issueCreateDto.getAssignees(), issue, false);
    }
    issue.setCreatedBy(currentUserLoggedIn.getUsername());
    issue.setCreatedAt(LocalDateTime.now());
    Image savedImage = uploadImage(imageFile, currentUserLoggedIn);
    issue.setImage(savedImage);
    Issue savedIssue = issueRepository.save(issue);
    IssueHistory issueHistory = storeToHistory(savedIssue, LocalDateTime.now());
    issueHistoryRepository.save(issueHistory);
    log.info("Save issue with image successfully");
    return new ResponseDto<>(issueMapping.convertIssueToIssueDto(savedIssue));
  }

  @Override
  public ResponseDto<IssueDto> getSingleIssue(Long issueId) {
//    User currentUserLoggedIn = commonService.getCurrentUserLoggedIn();
    Issue issue = getSingleIssueFromId(issueId);
//    if (!isCurrentUserAllowedToDoThisAction(issue, currentUserLoggedIn)) {
//      throw new NotAllowedException(commonService.getMessage(StringConstants.USER_NOT_ALLOWED));
//    }
    return new ResponseDto<>(issueMapping.convertIssueToIssueDto(issue));
  }

  @Override
  @Transactional
  @SneakyThrows
  public ResponseDto<IssueDto> updateIssue(Long issueId, IssueDto issueUpdateDto) {
    Issue currentIssue = getSingleIssueFromId(issueId);
    User currentUser = commonService.getCurrentUserLoggedIn();
    if (!isCurrentUserAllowedToDoThisAction(currentIssue, currentUser)) {
      throw new NotAllowedException(StringConstants.USER_NOT_ALLOWED);
    }
    updateDueDate(issueUpdateDto, currentIssue);
    updateIsAppendix(issueUpdateDto, currentIssue);
    updateIsDealCustomer(issueUpdateDto, currentIssue);
    updateVersion(issueUpdateDto, currentIssue);
    if (null != issueUpdateDto.getType()) {
      updateIssueType(issueUpdateDto.getType().getId(), currentIssue);
    }
    if (null != issueUpdateDto.getCustomer()) {
      updateCustomer(issueUpdateDto.getCustomer().getId(), currentIssue);
    }
    if (!issueUpdateDto.isAssigneeEmpty()) {
      updateAssignees(issueUpdateDto.getAssignees(), currentIssue, true);
      currentIssue.setStatus(IssueStatus.ASSIGNED);
    }
    updateStatus(issueUpdateDto, currentIssue);
    Issue updatedIssue = saveIssueHistoryAndIssue(currentIssue, currentUser);
    log.info("Update issue successfully");
    return new ResponseDto<>(issueMapping.convertIssueToIssueDto(updatedIssue));
  }

  @Override
  public ResponseDto<IssueDto> dealCustomer(Long issueId) {
    Issue currentIssue = getSingleIssueFromId(issueId);
    User currentUser = commonService.getCurrentUserLoggedIn();
    if (!isCurrentUserAllowedToDoThisAction(currentIssue, currentUser) && !currentIssue.isDone()) {
      throw new NotAllowedException(commonService.getMessage(StringConstants.USER_NOT_ALLOWED));
    }
    Issue updatedIssue = saveIssueHistoryAndIssue(currentIssue, currentUser);
    return new ResponseDto<>(issueMapping.convertIssueToIssueDto(updatedIssue));
  }

  @Override
  @Transactional
  public void getAndSaveIssuesFromPenpot() {
    List<SystemCodeDetail> penpotTokens = systemCodeDetailRepository.getListSystemCodeDetailByCode(
        PENPOT_TOKEN);
    if (penpotTokens.isEmpty()) {
      throw new NotAllowedException(commonService.getMessage(StringConstants.USER_NOT_ALLOWED));
    }
    for (SystemCodeDetail penpotToken : penpotTokens) {
      HttpEntity request = getHttpEntity(penpotToken.getCodeName());
      String url = PENPOT_API_URL + PENPOT_TEAMS;
      List<PenpotTeamDto> penpotTeamDtos = getPenpotTeams(url, request);
      for (PenpotTeamDto penpotTeamDto : penpotTeamDtos) {
        url = PENPOT_API_URL + PENPOT_TEAM_RECENT_FILES + "?team-id=" + penpotTeamDto.getId();
        List<PenpotFileDto> penpotFileDtos = getPenpotTeamFiles(url, request);
        for (PenpotFileDto penpotFileDto : penpotFileDtos) {
          url = PENPOT_API_URL + PENPOT_COMMENT_THREADS + "?file-id=" + penpotFileDto.getId();
          List<PenpotCommentDto> penpotCommentDtos = getPenpotCommentThreads(url, request);
          url = PENPOT_API_URL + PENPOT_GET_PAGE + "?file-id=" + penpotFileDto.getId();
          PenpotPageDto penpotPageDto = getPenpotPage(url, request);
          for (PenpotCommentDto penpotCommentDto : penpotCommentDtos) {
            if (isIssueExisted(penpotCommentDto)) {
              continue;
            }
            url = PENPOT_API_URL + PENPOT_FILE_OBJECT_THUMBNAILS + "?file-id="
                + penpotFileDto.getId();
            Map<String, String> mapObject = getFileObjectThumbnails(url, request);
            String imageId = extractImageIdFromObjectThumbnails(penpotCommentDto, mapObject);
            Image image = uploadImage(PENPOT_API_URL + PENPOT_ASSETS_BY_ID + '/' + imageId);
            saveIssueAndHistory(penpotCommentDto, penpotFileDto, penpotPageDto, image);
          }
        }
      }
    }
  }

  private void saveIssueAndHistory(PenpotCommentDto penpotCommentDto, PenpotFileDto penpotFileDto,
      PenpotPageDto penpotPageDto, Image image) {
    String penpotPrototypeLink = PENPOT_APP_URL + PENPOT_PROTOTYPE_LINK;
    Map<String, PenpotPageFlowDto> flows = penpotPageDto.getFlows();
    String key = flows.keySet().stream().findFirst().get();
    PenpotPageFlowDto penpotPageFlowDto = flows.get(key);
    LocalDateTime now = LocalDateTime.now();
    Issue issue = new Issue();
    issue.setPenpotPrototypeLink(
        String.format(penpotPrototypeLink, penpotFileDto.getId(), penpotPageDto.getId(),
            penpotPageFlowDto.getId()));
    issue.setStatus(IssueStatus.NEW);
    issue.setContent(penpotCommentDto.getContent());
    issue.setPenpotCommentId(penpotCommentDto.getId());
    issue.setCreatedAt(now);
    issue.setCreatedBy(StringConstants.SYSTEM);
    issue.setImage(image);
    Issue savedIssue = issueRepository.save(issue);
    IssueHistory issueHistory = storeToHistory(savedIssue, now);
    issueHistoryRepository.save(issueHistory);
  }

  private Issue getSingleIssueFromId(Long issueId) {
    return issueRepository.findById(issueId)
        .orElseThrow(
            () -> new NotFoundException(commonService.getMessage(StringConstants.ISSUE_NOT_FOUND)));
  }

  private void updateStatus(IssueDto issueDto, Issue currentIssue) {
    if (null == issueDto.getStatus()) {
      return;
    }
    switch (currentIssue.getStatus()) {
      case NEW -> changeFromNew(issueDto, currentIssue);
      case ASSIGNED -> changeFromAssigned(issueDto, currentIssue);
      case DISCUSSING -> changeFromDiscussing(issueDto, currentIssue);
      case ANALYST -> changeFromAnalyst(issueDto, currentIssue);
      case DONE -> changeFromDone(issueDto, currentIssue);
      case PENDING -> changeFromPending(issueDto, currentIssue);
      case CLOSE -> changeFromClose(issueDto, currentIssue);
      default ->
          throw new NotAllowedException(commonService.getMessage(StringConstants.USER_NOT_ALLOWED));
    }
  }

  private void changeFromNew(IssueDto issueUpdateDto, Issue currentIssue) {
    if (!issueUpdateDto.isAssigneeEmpty() && issueUpdateDto.isAssigned()) {
      updateAssignees(issueUpdateDto.getAssignees(), currentIssue, true);
      currentIssue.setStatus(IssueStatus.ASSIGNED);
      return;
    }
    if (issueUpdateDto.isPending() || issueUpdateDto.isClose()) {
      currentIssue.setStatus(issueUpdateDto.getStatus());
      return;
    }
    throw new NotAllowedException(commonService.getMessage(StringConstants.USER_NOT_ALLOWED));
  }

  private void changeFromAssigned(IssueDto issueUpdateDto, Issue currentIssue) {
    if (issueUpdateDto.isNew() || issueUpdateDto.isPending() || issueUpdateDto.isClose()) {
      currentIssue.setStatus(issueUpdateDto.getStatus());
      return;
    }
    throw new NotAllowedException(commonService.getMessage(StringConstants.USER_NOT_ALLOWED));
  }

  private void changeFromDiscussing(IssueDto issueUpdateDto, Issue currentIssue) {
    if (issueUpdateDto.isNew() && currentIssue.getAssignees().isEmpty()
        && getPostsByIssueId(currentIssue.getId()).isEmpty()) {
      currentIssue.setStatus(issueUpdateDto.getStatus());
      return;
    }
    if (issueUpdateDto.isAssigned() && !currentIssue.getAssignees().isEmpty()
        && getPostsByIssueId(currentIssue.getId()).isEmpty()) {
      currentIssue.setStatus(issueUpdateDto.getStatus());
      return;
    }
    if (issueUpdateDto.isPending() || issueUpdateDto.isClose()) {
      currentIssue.setStatus(issueUpdateDto.getStatus());
      return;
    }
    throw new NotAllowedException(commonService.getMessage(StringConstants.USER_NOT_ALLOWED));
  }

  private void changeFromAnalyst(IssueDto issueUpdateDto, Issue currentIssue) {
    if (issueUpdateDto.isPending() || issueUpdateDto.isClose()) {
      currentIssue.setStatus(issueUpdateDto.getStatus());
      return;
    }
    throw new NotAllowedException(commonService.getMessage(StringConstants.USER_NOT_ALLOWED));
  }

  private void changeFromDone(IssueDto issueUpdateDto, Issue currentIssue) {
    if (issueUpdateDto.isClose() || issueUpdateDto.isAnalyst()) {
      currentIssue.setStatus(issueUpdateDto.getStatus());
      return;
    }
    throw new NotAllowedException(commonService.getMessage(StringConstants.USER_NOT_ALLOWED));
  }

  private void changeFromPending(IssueDto issueUpdateDto, Issue currentIssue) {
    if (issueUpdateDto.isNew() || issueUpdateDto.isAssigned() || issueUpdateDto.isDiscussing()
        || issueUpdateDto.isAnalyst()
        || issueUpdateDto.isClose()) {
      currentIssue.setStatus(issueUpdateDto.getStatus());
      return;
    }
    throw new NotAllowedException(commonService.getMessage(StringConstants.USER_NOT_ALLOWED));
  }

  private void changeFromClose(IssueDto issueUpdateDto, Issue currentIssue) {
    if (issueUpdateDto.isNew() || issueUpdateDto.isAssigned() || issueUpdateDto.isDiscussing()
        || issueUpdateDto.isAnalyst()
        || issueUpdateDto.isPending() || issueUpdateDto.isDone()) {
      currentIssue.setStatus(issueUpdateDto.getStatus());
      return;
    }
    throw new NotAllowedException(commonService.getMessage(StringConstants.USER_NOT_ALLOWED));
  }

  private List<Post> getPostsByIssueId(Long issueId) {
    return postRepository.getListPostByIssueId(issueId);
  }

  @NotNull
  private HttpEntity getHttpEntity(String penpotToken) {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Token " + penpotToken);
    return new HttpEntity(headers);
  }

  private List<PenpotCommentDto> getPenpotCommentThreads(String url, HttpEntity request) {
    ResponseEntity<List<PenpotCommentDto>> listPenpotCommentDtoResponseEntity = restTemplate.exchange(
        url,
        HttpMethod.GET,
        request, new ParameterizedTypeReference<>() {
        });
    if (!listPenpotCommentDtoResponseEntity.getStatusCode().is2xxSuccessful()) {
      throw new PenpotException(commonService.getMessage(StringConstants.PENPOT_REQUEST_ERROR));
    }
    if (null == listPenpotCommentDtoResponseEntity.getBody()) {
      throw new PenpotException(commonService.getMessage(StringConstants.PENPOT_REQUEST_ERROR));
    }
    return listPenpotCommentDtoResponseEntity.getBody();
  }

  private List<PenpotTeamDto> getPenpotTeams(String url, HttpEntity request) {
    ResponseEntity<List<PenpotTeamDto>> penpotTeams = restTemplate.exchange(
        url,
        HttpMethod.GET,
        request, new ParameterizedTypeReference<>() {
        });
    if (!penpotTeams.getStatusCode().is2xxSuccessful()) {
      throw new PenpotException(commonService.getMessage(StringConstants.PENPOT_REQUEST_ERROR));
    }
    if (null == penpotTeams.getBody()) {
      throw new PenpotException(commonService.getMessage(StringConstants.PENPOT_REQUEST_ERROR));
    }
    return penpotTeams.getBody();
  }

  private PenpotPageDto getPenpotPage(String url, HttpEntity request) {
    ResponseEntity<PenpotPageDto> penpotPageDtoResponseEntity = restTemplate.exchange(
        url,
        HttpMethod.GET,
        request, new ParameterizedTypeReference<>() {
        });
    if (!penpotPageDtoResponseEntity.getStatusCode().is2xxSuccessful()) {
      throw new PenpotException(commonService.getMessage(StringConstants.PENPOT_REQUEST_ERROR));
    }
    if (null == penpotPageDtoResponseEntity.getBody()) {
      throw new PenpotException(commonService.getMessage(StringConstants.PENPOT_REQUEST_ERROR));
    }
    return penpotPageDtoResponseEntity.getBody();
  }

  private List<PenpotFileDto> getPenpotTeamFiles(String url, HttpEntity request) {
    ResponseEntity<List<PenpotFileDto>> penpotFileDtos = restTemplate.exchange(
        url,
        HttpMethod.GET,
        request, new ParameterizedTypeReference<>() {
        });
    if (!penpotFileDtos.getStatusCode().is2xxSuccessful()) {
      throw new PenpotException(commonService.getMessage(StringConstants.PENPOT_REQUEST_ERROR));
    }
    if (null == penpotFileDtos.getBody()) {
      throw new PenpotException(commonService.getMessage(StringConstants.PENPOT_REQUEST_ERROR));
    }
    return penpotFileDtos.getBody();
  }

  private Map<String, String> getFileObjectThumbnails(String url, HttpEntity request) {
    ResponseEntity<Map<String, String>> mapObjectEntity = restTemplate.exchange(
        url,
        HttpMethod.GET,
        request, new ParameterizedTypeReference<>() {
        });
    if (!mapObjectEntity.getStatusCode().is2xxSuccessful()) {
      throw new PenpotException(commonService.getMessage(StringConstants.PENPOT_REQUEST_ERROR));
    }
    if (null == mapObjectEntity.getBody()) {
      throw new PenpotException(commonService.getMessage(StringConstants.PENPOT_REQUEST_ERROR));
    }
    return mapObjectEntity.getBody();
  }

  private String extractImageIdFromObjectThumbnails(PenpotCommentDto penpotCommentDto,
      Map<String, String> mapObject) {
    String imageId = "";
    for (Entry<String, String> entry : mapObject.entrySet()) {
      if (entry.getKey().contains(penpotCommentDto.getFrameId())) {
        imageId = entry.getValue();
      }
    }
    if (imageId.isEmpty()) {
      throw new PenpotException(commonService.getMessage(StringConstants.PENPOT_REQUEST_ERROR));
    }
    return imageId;
  }

  private void updateVersion(IssueDto issueUpdateDto, Issue currentIssue) {
    currentIssue.setVersion(null == issueUpdateDto.getVersion() ? currentIssue.getVersion()
        : issueUpdateDto.getVersion());
  }

  private void updateCustomer(Long customerId, Issue currentIssue) {
    if (customerId == null) {
      throw new NotAllowedException(commonService.getMessage(StringConstants.USER_NOT_ALLOWED));
    }
    SystemCodeDetail customer = systemCodeDetailRepository.findById(customerId)
        .orElseThrow(() -> new NotAllowedException(
            commonService.getMessage(StringConstants.USER_NOT_ALLOWED)));
    currentIssue.setCustomer(customer);
  }

  private void updateIssueType(Long typeId, Issue currentIssue) {
    if (typeId == null) {
      throw new NotFoundException(commonService.getMessage(StringConstants.TYPE_NOT_FOUND));
    }
    SystemCodeDetail type = systemCodeDetailRepository.findById(typeId)
        .orElseThrow(
            () -> new NotFoundException(commonService.getMessage(StringConstants.TYPE_NOT_FOUND)));
    currentIssue.setType(type);
  }

  private void updateIsDealCustomer(IssueDto issueUpdateDto, Issue currentIssue) {
    if (null != issueUpdateDto.getIsDealCustomer() && !currentIssue.isDone()) {
      throw new NotAllowedException(commonService.getMessage(StringConstants.USER_NOT_ALLOWED));
    }
    if (null != issueUpdateDto.getIsDealCustomer() && currentIssue.isDone()) {
      currentIssue.setIsDealCustomer(issueUpdateDto.getIsDealCustomer());
    }
  }

  private void updateAssignees(List<UserDto> newAssignees, Issue issue, boolean isUpdate) {
    List<Long> userIdList = newAssignees.stream().map(IdDto::getId).toList();
    List<User> users = userRepository.findAllById(userIdList);
    if (isUpdate && !issue.getAssignees().isEmpty()) {
      issue.removeListUser(issue.getAssignees());
    }
    issue.addListUser(users);
  }

  private void setStatusWhenCreateIssue(IssueDto issueCreateDto, Issue issue) {
    if (!issueCreateDto.isAssigneeEmpty()) {
      updateAssignees(issueCreateDto.getAssignees(), issue, true);
      issue.setStatus(IssueStatus.ASSIGNED);
      issueRepository.save(issue);
    }
  }

  private void updateIsAppendix(IssueDto issueUpdateDto, Issue currentIssue) {
    currentIssue.setIsAppendix(null == issueUpdateDto.getIsAppendix() ? currentIssue.getIsAppendix()
        : issueUpdateDto.getIsAppendix());
  }

  private void updateDueDate(IssueDto issueUpdateDto, Issue currentIssue) {
    if (issueUpdateDto.getDueDate() != null) {
      if (TimeUtil.isBeforeDateNow(issueUpdateDto.getDueDate())) {
        throw new DateTimeException(
            commonService.getMessage(StringConstants.DUE_DATE_NOT_ACCEPTABLE));
      }
      currentIssue.setDueDate(issueUpdateDto.getDueDate());
    }
  }

  private boolean isCurrentUserNotAdminAndOneOfAssignees(Issue currentIssue, User currentUser) {
    return currentUser.isUser() && commonService.isCurrentUserLoggedInOneOfUsers(
        currentIssue.getAssignees(), currentUser);
  }

  private boolean isCurrentUserAllowedToDoThisAction(Issue currentIssue, User currentUser) {
    return currentUser.isAdmin() || isCurrentUserNotAdminAndOneOfAssignees(currentIssue,
        currentUser);
  }

  private boolean isFromDateAndToDateNotValid(IssueViewRequest issueViewRequest) {
    return issueViewRequest.getFromDate() != null && issueViewRequest.getToDate() != null &&
        issueViewRequest.getToDate().isBefore(issueViewRequest.getFromDate());
  }

  private boolean isIssueExisted(PenpotCommentDto penpotCommentDto) {
    return issueRepository.existsIssueByPenpotCommentId(penpotCommentDto.getId()).isPresent();
  }

  private boolean isDateTypeNotAllowed(IssueViewRequest issueViewRequest) {
    return StringConstants.ISSUE_DATE_TYPE_ACCEPT.stream()
        .noneMatch(d -> d.equalsIgnoreCase(issueViewRequest.getTypeDate()));
  }

  @SneakyThrows
  private IssueHistory storeToHistory(Issue issue, LocalDateTime now) {
    IssueDto issueDto = issueMapping.convertIssueToIssueDto(issue);
    String issueOld = objectMapper.writeValueAsString(issueDto);
    IssueHistory issueHistory = new IssueHistory();
    issueHistory.setIssue(issue);
    issueHistory.setContent(issueOld);
    issueHistory.setModifiedBy(issue.getCreatedBy());
    issueHistory.setModifiedAt(now);
    return issueHistory;
  }

  private Issue saveIssueHistoryAndIssue(Issue currentIssue, User currentUser) {
    LocalDateTime now = LocalDateTime.now();
    IssueHistory issueHistory = storeToHistory(currentIssue, now);
    issueHistory.setModifiedAt(now);
    issueHistory.setModifiedBy(currentUser.getUsername());
    issueHistoryRepository.save(issueHistory);
    currentIssue.addHistory(issueHistory);
    return issueRepository.save(currentIssue);
  }

  @SneakyThrows
  private Image uploadImage(String url) {
    String bucketName = minioConfiguration.getBucketName();
    String fileName = url.substring(url.lastIndexOf('/') + 1);
    try {
      String fullName = minioUtil.putFileUrl(bucketName, url, fileName);
      Image image = new Image();
      image.setImageUrl(fullName);
      image.setCreatedAt(LocalDateTime.now());
      image.setCreatedBy(StringConstants.SYSTEM);
      return imageRepository.save(image);
    } catch (Exception e) {
      minioUtil.removeObject(bucketName, fileName);
      throw new MinioException(commonService.getMessage(StringConstants.MINIO_ISSUE));
    }
  }

  @SneakyThrows
  private Image uploadImage(MultipartFile file, User currentUserLoggedIn) {
    String fileName = file.getOriginalFilename();
    assert fileName != null;
    String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
    String objectName = UUID.randomUUID().toString().replace("-", "") + "." + fileType;
    String bucketName = minioConfiguration.getBucketName();
    try {
      minioUtil.putObject(bucketName, file, objectName);
      log.info("Uploading image successfully");
      LocalDateTime now = LocalDateTime.now();
      Image image = new Image();
      image.setImageUrl(objectName);
      image.setCreatedAt(now);
      image.setCreatedBy(currentUserLoggedIn.getUsername());
      return imageRepository.save(image);
    } catch (Exception e) {
      minioUtil.removeObject(bucketName, objectName);
      log.error(e.getMessage());
      throw new MinioException(commonService.getMessage(StringConstants.MINIO_ISSUE));
    }
  }
}
