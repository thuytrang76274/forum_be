package com.ptithcm.forum.mapping;

import com.ptithcm.forum.dto.IssueDto;
import com.ptithcm.forum.entity.Issue;

public interface IssueMapping {

  Issue convertIssueDtoToIssue(IssueDto issueDto);

  IssueDto convertIssueToIssueDto(Issue issue);
}
