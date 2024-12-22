package com.ptithcm.forum.service;

import com.ptithcm.forum.dto.CommentDto;
import com.ptithcm.forum.dto.ResponseDto;
import java.util.List;

public interface CommentService {

  ResponseDto<CommentDto> getSingleComment(Long commentId);

  ResponseDto<CommentDto> createComment(CommentDto commentDto);

  ResponseDto<CommentDto> updateComment(Long commentId, CommentDto commentDto);

  ResponseDto<CommentDto> pinComment(Long commentId);

  ResponseDto<CommentDto> unpinComment(Long commentId);

  ResponseDto<List<CommentDto>> getCommentsByPost(Long postId, boolean isSolution);

  void deleteComment(Long commentId);
}
