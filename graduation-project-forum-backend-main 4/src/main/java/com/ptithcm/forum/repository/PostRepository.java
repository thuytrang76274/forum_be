package com.ptithcm.forum.repository;

import com.ptithcm.forum.entity.Post;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryExtend {

  @Query("select p from Post p join fetch p.issue where p.issue.id = :issueId")
  List<Post> getListPostByIssueId(Long issueId);

  @Query("select p from Post p "
      + "left join fetch p.module m "
      + "left join fetch p.comments cu "
      + "left join fetch m.systemCode sm "
      + "left join fetch p.issue i "
      + "left join fetch i.assignees asi "
      + "left join fetch i.customer c "
      + "left join fetch c.systemCode sc "
      + "left join fetch i.type t "
      + "left join fetch t.systemCode st "
      + "left join fetch i.image im "
      + "where i.id = :issueId")
  List<Post> getListPostDetailByIssueId(Long issueId);

  @Query("select p from Post p "
      + "left join fetch p.module m "
      + "left join fetch p.comments cu "
      + "left join fetch m.systemCode sm "
      + "left join fetch p.issue i "
      + "left join fetch i.assignees asi "
      + "left join fetch i.customer c "
      + "left join fetch c.systemCode sc "
      + "left join fetch i.type t "
      + "left join fetch t.systemCode st "
      + "left join fetch i.image im "
      + "where p.id = :id")
  Optional<Post> findPostDetailById(Long id);
}
