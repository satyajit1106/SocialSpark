package com.hashedin.huSpark.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ReportMetricsResponse {
    private Long pendingReports;
    private Long approvedReports;
    private Long rejectedReports;
}
