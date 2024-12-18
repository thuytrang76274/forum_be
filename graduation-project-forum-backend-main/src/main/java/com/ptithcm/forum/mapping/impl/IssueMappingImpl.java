package com.ptithcm.forum.mapping.impl;

import com.ptithcm.forum.dto.IssueDto;
import com.ptithcm.forum.entity.Issue;
import com.ptithcm.forum.mapping.ImageMapping;
import com.ptithcm.forum.mapping.IssueMapping;
import com.ptithcm.forum.mapping.SystemCodeDetailMapping;
import com.ptithcm.forum.mapping.UserMapping;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class IssueMappingImpl implements IssueMapping {

  private final ImageMapping imageMapping;
  private final UserMapping userMapping;
  private final SystemCodeDetailMapping systemCodeDetailMapping;

  public IssueMappingImpl(ImageMapping imageMapping, UserMapping userMapping,
      SystemCodeDetailMapping systemCodeDetailMapping) {
    this.imageMapping = imageMapping;
    this.userMapping = userMapping;
    this.systemCodeDetailMapping = systemCodeDetailMapping;
  }

  @Override
  public Issue convertIssueDtoToIssue(IssueDto issueDto) {
    if (issueDto == null) {
      return null;
    }
    Issue issue = new Issue();
    issue.setContent(issueDto.getContent());
    issue.setDueDate(issueDto.getDueDate());
    issue.setStatus(issueDto.getStatus());
    issue.setVersion(issueDto.getVersion());
    return issue;
  }

  @Override
  public IssueDto convertIssueToIssueDto(Issue issue) {
    if (issue == null) {
      return null;
    }
    IssueDto issueDto = new IssueDto();
    issueDto.setId(issue.getId());
    issueDto.setPenpotCommentId(issue.getPenpotCommentId());
    issueDto.setContent(issue.getContent());
    issueDto.setDueDate(issue.getDueDate());
    issueDto.setStatus(issue.getStatus());
    issueDto.setVersion(issue.getVersion());
    issueDto.setIsAppendix(issue.getIsAppendix());
    issueDto.setIsDealCustomer(issue.getIsDealCustomer());
    issueDto.setCustomer(
        systemCodeDetailMapping.convertSystemCodeDetailToSystemCodeDetailDto(issue.getCustomer()));
    issueDto.setCreatedAt(issue.getCreatedAt());
    issueDto.setCreatedBy(issue.getCreatedBy());
    issueDto.setType(
        systemCodeDetailMapping.convertSystemCodeDetailToSystemCodeDetailDto(issue.getType()));
    issueDto.setImage(imageMapping.convertImageToImageDto(issue.getImage()));
    issueDto.setAssignees(
        issue.getAssignees().stream().map(userMapping::convertUserToUserDtoView).collect(
            Collectors.toList()));
    return issueDto;
  }
}
