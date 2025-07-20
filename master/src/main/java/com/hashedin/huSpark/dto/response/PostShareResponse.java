package com.hashedin.huSpark.dto.response;

import com.hashedin.huSpark.entity.Post;
import com.hashedin.huSpark.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class PostShareResponse {
    private Long id;
    private User user;
    private Post post;
}
