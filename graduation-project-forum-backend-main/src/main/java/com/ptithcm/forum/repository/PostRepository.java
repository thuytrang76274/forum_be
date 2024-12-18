package com.ptithcm.forum.repository;

import com.ptithcm.forum.entity.Post;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryExtend {

  @Query("select p from Post p join fetch p.issue where p.issue.id = :issueId")
  List<Post> getListPostByIssueId(Long issueId);
}
