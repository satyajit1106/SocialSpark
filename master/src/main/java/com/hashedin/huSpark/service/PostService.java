package com.hashedin.huSpark.service;

import com.hashedin.huSpark.dto.request.PostRequest;
import com.hashedin.huSpark.dto.request.PostUpdateRequest;
import com.hashedin.huSpark.dto.response.PostMetricsResponse;
import com.hashedin.huSpark.dto.response.PostResponse;
import com.hashedin.huSpark.dto.response.PostShareResponse;
import com.hashedin.huSpark.entity.Post;

import java.util.Optional;

public interface PostService {
    PostResponse createPost(PostRequest postRequest, String filePath);

    void deletePostById(Long postId);

    PostResponse likePost(Long postId);

    PostResponse getPost(Long postId);

    PostMetricsResponse getMetrics(Long postId);

    PostShareResponse sharePost(Long postId);

    PostResponse updatePost(PostUpdateRequest postUpdateRequest, String filePath);
}
