package com.ptithcm.forum.mapping.impl;

import com.ptithcm.forum.dto.CommentDto;
import com.ptithcm.forum.entity.Comment;
import com.ptithcm.forum.mapping.CommentMapping;
import com.ptithcm.forum.mapping.PostMapping;
import org.springframework.stereotype.Component;

@Component
public class CommentMappingImpl implements CommentMapping {

  private final PostMapping postMapping;

  public CommentMappingImpl(PostMapping postMapping) {
    this.postMapping = postMapping;
  }

  @Override
  public Comment convertCommentDtoToComment(CommentDto commentDto) {
    if (commentDto == null) {
      return null;
    }
    Comment comment = new Comment();
    comment.setContent(commentDto.getContent());
    return comment;
  }

  @Override
  public CommentDto convertCommentToCommentDto(Comment comment) {
    if (comment == null) {
      return null;
    }
    CommentDto commentDto = new CommentDto();
    commentDto.setId(comment.getId());
    commentDto.setContent(comment.getContent());
    commentDto.setVote(comment.getVote());
    commentDto.setIsSolution(comment.getIsSolution());
    commentDto.setPost(postMapping.convertPostToPostDto(comment.getPost()));
    commentDto.setCreatedAt(comment.getCreatedAt());
    commentDto.setCreatedBy(comment.getCreatedBy());
    commentDto.setModifiedAt(comment.getModifiedAt());
    commentDto.setModifiedBy(comment.getModifiedBy());
    return commentDto;
  }
}
