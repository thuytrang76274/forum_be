package com.ptithcm.forum.mapping.impl;

import com.ptithcm.forum.dto.PostDto;
import com.ptithcm.forum.entity.Post;
import com.ptithcm.forum.mapping.IssueMapping;
import com.ptithcm.forum.mapping.PostMapping;
import com.ptithcm.forum.mapping.SystemCodeDetailMapping;
import org.springframework.stereotype.Component;

@Component
public class PostMappingImpl implements PostMapping {

  private final IssueMapping issueMapping;
  private final SystemCodeDetailMapping systemCodeDetailMapping;

  public PostMappingImpl(IssueMapping issueMapping,
      SystemCodeDetailMapping systemCodeDetailMapping) {
    this.issueMapping = issueMapping;
    this.systemCodeDetailMapping = systemCodeDetailMapping;
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
    postDto.setDescription(post.getDescription());
    postDto.setIssue(issueMapping.convertIssueToIssueDto(post.getIssue()));
    postDto.setModule(
        systemCodeDetailMapping.convertSystemCodeDetailToSystemCodeDetailDto(post.getModule()));
    postDto.setApprovedAt(post.getApprovedAt());
    postDto.setCreatedAt(post.getCreatedAt());
    postDto.setModifiedAt(post.getModifiedAt());
    postDto.setCreatedAt(post.getCreatedAt());
    postDto.setCreatedBy(post.getCreatedBy());
    return postDto;
  }
}
