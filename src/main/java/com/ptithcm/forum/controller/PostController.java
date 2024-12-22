package com.ptithcm.forum.controller;

import com.ptithcm.forum.dto.PageDto;
import com.ptithcm.forum.dto.PostDto;
import com.ptithcm.forum.dto.PostViewRequest;
import com.ptithcm.forum.dto.ResponseDto;
import com.ptithcm.forum.entity.PostStatus;
import com.ptithcm.forum.service.PostService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${rest.post}")
public class PostController {

  private final PostService postService;

  public PostController(PostService postService) {
    this.postService = postService;
  }

  @GetMapping
  public ResponseDto<PageDto<PostDto>> findAll(
      @RequestParam(name = "page", required = false, defaultValue = "0") int page,
      @RequestParam(name = "size", required = false, defaultValue = "10") int size,
      @RequestParam(name = "typeDate", defaultValue = "dueDate") String typeDate,
      @RequestParam(name = "fromDate", required = false) LocalDate fromDate,
      @RequestParam(name = "toDate", required = false) LocalDate toDate,
      @RequestParam(name = "status", required = false) PostStatus status,
      @RequestParam(name = "reporter", required = false) List<String> reporter,
      @RequestParam(name = "assignee", required = false) List<Long> assigneeIds,
      @RequestParam(name = "type", required = false) Long typeId,
      @RequestParam(name = "customer", required = false) Long customerId,
      @RequestParam(name = "module", required = false) Long moduleId,
      @RequestParam(name = "version", required = false) String version,
      @RequestParam(name = "isDealCustomer", required = false) Boolean isDealCustomer,
      @RequestParam(name = "isAppendix", required = false) Boolean isAppendix
  ) {
    return postService.getPageOfPosts(PostViewRequest.builder()
        .page(page).size(size).typeDate(typeDate).fromDate(fromDate).toDate(toDate).status(status)
        .reporter(reporter).assigneeIds(assigneeIds).typeId(typeId).customerId(customerId)
        .moduleId(moduleId).version(version).isDealCustomer(isDealCustomer).isAppendix(isAppendix)
        .build());
  }

  @GetMapping("/issue/{issueId}")
  public ResponseDto<List<PostDto>> findById(@PathVariable Long issueId) {
    return postService.getPostsByIssueId(issueId);
  }

  @GetMapping("/{id}")
  public ResponseDto<PostDto> getSinglePost(@PathVariable("id") Long postId) {
    return postService.getSinglePost(postId);
  }

  @PostMapping
  @Validated
  public ResponseEntity<ResponseDto<PostDto>> createPost(@RequestBody @Valid PostDto postDto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(postService.createPost(postDto));
  }

  @PatchMapping("/{id}")
  public ResponseDto<PostDto> updatePost(@PathVariable("id") Long postId,
      @RequestBody PostDto postDto) {
    return postService.updatePost(postId, postDto);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Object> deletePost(@PathVariable("id") Long postId) {
    postService.deletePost(postId);
    return ResponseEntity.accepted().build();
  }
}
