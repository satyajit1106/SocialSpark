package com.hashedin.huSpark.repository;

import com.hashedin.huSpark.entity.PostReport;
import com.hashedin.huSpark.entity.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostReportRepository extends JpaRepository<PostReport, Long> {
    List<PostReport> findByStatus(ReportStatus status);
}
