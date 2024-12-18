package com.ptithcm.forum.service;

import com.ptithcm.forum.dto.PageDto;
import com.ptithcm.forum.dto.PostDto;
import com.ptithcm.forum.dto.PostViewRequest;
import com.ptithcm.forum.dto.ResponseDto;

public interface PostService {

  ResponseDto<PostDto> createPost(PostDto postDto);

  ResponseDto<PageDto<PostDto>> getPageOfPosts(PostViewRequest postViewRequest);

  ResponseDto<PostDto> updatePost(Long postId, PostDto postDto);

  ResponseDto<PostDto> getSinglePost(Long postId);

  void deletePost(Long postId);
}
