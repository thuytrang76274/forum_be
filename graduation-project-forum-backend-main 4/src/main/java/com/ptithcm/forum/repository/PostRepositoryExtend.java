package com.ptithcm.forum.repository;

import com.ptithcm.forum.dto.PostViewRequest;
import com.ptithcm.forum.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryExtend {
  Page<Post> getPostsByConditions(PostViewRequest postViewRequest, Pageable pageable);
}
