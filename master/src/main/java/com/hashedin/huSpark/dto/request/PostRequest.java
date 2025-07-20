package com.hashedin.huSpark.dto.request;

import com.hashedin.huSpark.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostRequest {
    private String text;
}
