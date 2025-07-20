package com.hashedin.huSpark.service;

import com.hashedin.huSpark.dto.response.ReportMetricsResponse;
import com.hashedin.huSpark.entity.PostReport;

import java.util.List;

public interface PostReportService {
    PostReport reportPost(Long postId, String reason);

    List<PostReport> getPendingReports();

    void approveReport(Long reportId);

    void rejectReport(Long reportId);

    ReportMetricsResponse getMetrics();
}
