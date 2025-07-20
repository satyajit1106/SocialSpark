package com.hashedin.huSpark.service;

import com.hashedin.huSpark.dto.response.CommentResponse;
import com.hashedin.huSpark.entity.Comment;

import java.util.List;

public interface CommentService {
    List<CommentResponse> getPostComments(Long postId);

    CommentResponse addComment(Long postId, String text);

    void removeComment(Long commentId, Long postId);
}
