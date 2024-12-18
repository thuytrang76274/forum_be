package com.ptithcm.forum.mapping;

import com.ptithcm.forum.dto.CommentDto;
import com.ptithcm.forum.entity.Comment;

public interface CommentMapping {

  Comment convertCommentDtoToComment(CommentDto commentDto);

  CommentDto convertCommentToCommentDto(Comment comment);
}
