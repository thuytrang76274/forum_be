package com.ptithcm.forum.controller;

import com.ptithcm.forum.dto.CommentDto;
import com.ptithcm.forum.dto.ResponseDto;
import com.ptithcm.forum.service.CommentService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${rest.comment}")
public class CommentController {

  private final CommentService commentService;

  public CommentController(CommentService commentService) {
    this.commentService = commentService;
  }

  @GetMapping("/{id}")
  public ResponseDto<CommentDto> getSingleComment(@PathVariable("id") Long commentId) {
    return commentService.getSingleComment(commentId);
  }

  @GetMapping("/post/{id}")
  public ResponseDto<List<CommentDto>> getPostComment(@PathVariable("id") Long postId) {
    return commentService.getCommentsByPost(postId, false);
  }

  @PostMapping
  public ResponseEntity<ResponseDto<CommentDto>> createComment(@RequestBody CommentDto commentDto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(commentService.createComment(commentDto));
  }

  @PatchMapping("/{id}")
  public ResponseDto<CommentDto> updateComment(@PathVariable("id") Long commentId,
      @RequestBody CommentDto commentDto) {
    return commentService.updateComment(commentId, commentDto);
  }

  @PatchMapping("/{id}/pin")
  public ResponseDto<CommentDto> pinComment(@PathVariable("id") Long commentId) {
    return commentService.pinComment(commentId);
  }

  @PatchMapping("/{id}/unpin")
  public ResponseDto<CommentDto> unpinComment(@PathVariable("id") Long commentId) {
    return commentService.unpinComment(commentId);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteComment(@PathVariable("id") Long commentId) {
    commentService.deleteComment(commentId);
    return ResponseEntity.noContent().build();
  }
}
