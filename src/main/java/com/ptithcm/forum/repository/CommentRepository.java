package com.ptithcm.forum.repository;

import com.ptithcm.forum.entity.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends JpaRepository<Comment, Long> {
  @Query("select c from Comment c join fetch c.post p where c.isSolution = true and p.id = :postId")
  List<Comment> getSolutionCommentsByPostId(Long postId);

  @Query("select c from Comment c join fetch c.post p where p.id = :postId")
  List<Comment> getCommentSolutionsByPost(Long postId);
}
