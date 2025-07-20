package com.hashedin.huSpark.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class FollowResponse {
    private Long followerId;
    private Long followedUserId;
    private LocalDateTime followedAt;
}
