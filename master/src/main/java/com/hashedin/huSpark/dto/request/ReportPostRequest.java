package com.hashedin.huSpark.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ReportPostRequest {
    private String reason;
}
