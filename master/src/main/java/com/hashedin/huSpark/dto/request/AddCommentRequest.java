package com.hashedin.huSpark.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddCommentRequest {
    private String text;
}
