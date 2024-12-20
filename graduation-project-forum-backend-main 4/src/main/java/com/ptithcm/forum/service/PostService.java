package com.ptithcm.forum.service;

import com.ptithcm.forum.dto.PageDto;
import com.ptithcm.forum.dto.PostDto;
import com.ptithcm.forum.dto.PostViewRequest;
import com.ptithcm.forum.dto.ResponseDto;
import java.util.List;

public interface PostService {

  ResponseDto<PostDto> createPost(PostDto postDto);

  ResponseDto<PageDto<PostDto>> getPageOfPosts(PostViewRequest postViewRequest);

  ResponseDto<PostDto> updatePost(Long postId, PostDto postDto);

  ResponseDto<PostDto> getSinglePost(Long postId);

  ResponseDto<List<PostDto>> getPostsByIssueId(Long issueId);

  void deletePost(Long postId);
}
