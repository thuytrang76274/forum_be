package com.ptithcm.forum.repository;

import com.ptithcm.forum.dto.IssueViewRequest;
import com.ptithcm.forum.entity.Issue;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IssueRepositoryExtend {

  Page<Issue> getIssuesByConditions(IssueViewRequest request, Pageable pageable);
}
