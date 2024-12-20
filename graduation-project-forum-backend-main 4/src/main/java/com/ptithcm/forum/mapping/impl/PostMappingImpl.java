package com.ptithcm.forum.mapping.impl;

import com.ptithcm.forum.dto.PostDto;
import com.ptithcm.forum.entity.Post;
import com.ptithcm.forum.mapping.CommentMapping;
import com.ptithcm.forum.mapping.IssueMapping;
import com.ptithcm.forum.mapping.PostMapping;
import com.ptithcm.forum.mapping.SystemCodeDetailMapping;
import org.springframework.stereotype.Component;

@Component
public class PostMappingImpl implements PostMapping {

  private final IssueMapping issueMapping;
  private final SystemCodeDetailMapping systemCodeDetailMapping;
  private final CommentMapping commentMapping;

  public PostMappingImpl(IssueMapping issueMapping,
      SystemCodeDetailMapping systemCodeDetailMapping, CommentMapping commentMapping) {
    this.issueMapping = issueMapping;
    this.systemCodeDetailMapping = systemCodeDetailMapping;
    this.commentMapping = commentMapping;
  }

  @Override
  public Post convertPostDtoToPost(PostDto postDto) {
    if (postDto == null) {
      return null;
    }
    Post post = new Post();
    post.setApplyFor(postDto.getApplyFor());
    post.setTitle(postDto.getTitle());
    post.setDescription(postDto.getDescription());
    return post;
  }

  @Override
  public PostDto convertPostToPostDto(Post post) {
    if (post == null) {
      return null;
    }
    PostDto postDto = new PostDto();
    postDto.setId(post.getId());
    postDto.setApplyFor(post.getApplyFor());
    postDto.setTitle(post.getTitle());
    postDto.setStatus(post.getStatus());
    postDto.setDescription(post.getDescription());
    postDto.setIssue(issueMapping.convertIssueToIssueDto(post.getIssue()));
    postDto.setModule(
        systemCodeDetailMapping.convertSystemCodeDetailToSystemCodeDetailDto(post.getModule()));
    postDto.setApprovedAt(post.getApprovedAt());
    postDto.setCreatedAt(post.getCreatedAt());
    postDto.setModifiedAt(post.getModifiedAt());
    postDto.setCreatedBy(post.getCreatedBy());
    postDto.setModifiedBy(post.getModifiedBy());
    return postDto;
  }

  @Override
  public PostDto convertPostToPostDtoWithComments(Post post) {
    if (post == null) {
      return null;
    }
    PostDto postDto = convertPostToPostDto(post);
    postDto.setComments(
        post.getComments().stream().map(commentMapping::convertCommentToCommentDto)
            .toList());
    return postDto;
  }
}
