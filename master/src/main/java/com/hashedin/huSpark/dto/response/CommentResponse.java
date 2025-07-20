package com.hashedin.huSpark.dto.response;

import com.hashedin.huSpark.entity.Post;
import com.hashedin.huSpark.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class CommentResponse {
    private Long id;
    private String text;
    private User user;
    private Post post;
    private LocalDateTime createdAt;
}
