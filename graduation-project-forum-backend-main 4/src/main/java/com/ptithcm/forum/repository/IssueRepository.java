package com.ptithcm.forum.repository;

import com.ptithcm.forum.entity.Issue;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IssueRepository extends JpaRepository<Issue, Long>, IssueRepositoryExtend {

  @Query("select iss from Issue iss "
      + "join fetch iss.image "
      + "join fetch iss.type "
      + "join fetch iss.assignees "
      + "where iss.id = :issueId")
  Optional<Issue> findSingleIssueByIssueId(Long issueId);

  @Query("select i from Issue i where i.penpotCommentId = :penpotCommentId")
  Optional<Issue> existsIssueByPenpotCommentId(String penpotCommentId);
}
