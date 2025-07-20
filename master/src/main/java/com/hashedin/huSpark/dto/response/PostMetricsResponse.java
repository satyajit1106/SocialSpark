package com.hashedin.huSpark.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class PostMetricsResponse {
    private Long id;
    private Long comments;
    private Long likes;
    private Long shares;
}
