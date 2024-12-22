package com.ptithcm.forum.mapping;

import com.ptithcm.forum.dto.PostDto;
import com.ptithcm.forum.entity.Post;

public interface PostMapping {

  Post convertPostDtoToPost(PostDto postDto);

  PostDto convertPostToPostDto(Post post);

  PostDto convertPostToPostDtoWithComments(Post post);
}
