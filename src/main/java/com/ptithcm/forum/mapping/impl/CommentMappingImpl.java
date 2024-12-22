package com.ptithcm.forum.mapping.impl;

import com.ptithcm.forum.dto.CommentDto;
import com.ptithcm.forum.entity.Comment;
import com.ptithcm.forum.mapping.CommentMapping;
import org.springframework.stereotype.Component;

@Component
public class CommentMappingImpl implements CommentMapping {

  @Override
  public CommentDto convertCommentToCommentDto(Comment comment) {
    if (comment == null) {
      return null;
    }
    return mappingWithoutRecursive(comment);
  }

  private static CommentDto mappingWithoutRecursive(Comment comment) {
    CommentDto commentDto = new CommentDto();
    commentDto.setId(comment.getId());
    commentDto.setContent(comment.getContent());
    commentDto.setVote(comment.getVote());
    commentDto.setIsSolution(comment.getIsSolution());
    commentDto.setCreatedAt(comment.getCreatedAt());
    commentDto.setCreatedBy(comment.getCreatedBy());
    commentDto.setModifiedAt(comment.getModifiedAt());
    commentDto.setModifiedBy(comment.getModifiedBy());
    return commentDto;
  }
}
