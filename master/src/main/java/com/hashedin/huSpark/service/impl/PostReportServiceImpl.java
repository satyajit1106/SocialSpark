package com.hashedin.huSpark.service.impl;

import com.hashedin.huSpark.dto.response.ReportMetricsResponse;
import com.hashedin.huSpark.entity.Post;
import com.hashedin.huSpark.entity.PostReport;
import com.hashedin.huSpark.entity.ReportStatus;
import com.hashedin.huSpark.entity.User;
import com.hashedin.huSpark.repository.PostReportRepository;
import com.hashedin.huSpark.repository.PostRepository;
import com.hashedin.huSpark.repository.UserRepository;
import com.hashedin.huSpark.service.PostReportService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PostReportServiceImpl implements PostReportService {
    private final PostReportRepository reportRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    /**
     * @param postId
     * @param reason
     * @return
     */
    @Override
    public PostReport reportPost(Long postId, String reason) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<User> user =  userRepository.findByEmailIgnoreCase(email);

        if(user.isEmpty()) {
            throw new EntityNotFoundException("User does not exist.");
        }

        User existingUser = user.get();

        if(!existingUser.isActive()) {
            throw new RuntimeException("User account was deleted recently.");
        }

        Optional<Post> post = postRepository.findById(postId);

        if(post.isEmpty()) {
            throw new EntityNotFoundException("Post does not exist.");
        }

        Post existingPost = post.get();

        if(existingPost.isDeleted()) {
            throw new RuntimeException("The post you are trying to report is already deleted.");
        }

        PostReport report = PostReport
                .builder()
                .reportedBy(existingUser)
                .status(ReportStatus.PENDING)
                .reason(reason)
                .post(existingPost)
                .status(ReportStatus.PENDING)
                .reportedAt(LocalDateTime.now())
                .build();

        return reportRepository.save(report);
    }

    /**
     * @return
     */
    @Override
    public List<PostReport> getPendingReports() {
        return reportRepository.findByStatus(ReportStatus.PENDING);
    }

    /**
     * @param reportId
     */
    @Override
    public void approveReport(Long reportId) {
        Optional<PostReport> report = reportRepository.findById(reportId);

        if(report.isEmpty()) {
            throw new EntityNotFoundException("Report with the provided ID not found!");
        }

        PostReport existingReport = report.get();

        existingReport.setStatus(ReportStatus.APPROVED);
        reportRepository.save(existingReport);
    }

    /**
     * @param reportId
     */
    @Override
    public void rejectReport(Long reportId) {
        Optional<PostReport> report = reportRepository.findById(reportId);

        if(report.isEmpty()) {
            throw new EntityNotFoundException("Report with the provided ID not found!");
        }

        PostReport existingReport = report.get();

        existingReport.setStatus(ReportStatus.REJECTED);
        reportRepository.save(existingReport);
    }

    /**
     * @return
     */
    @Override
    public ReportMetricsResponse getMetrics() {
        return ReportMetricsResponse
                .builder()
                .pendingReports((long) reportRepository.findByStatus(ReportStatus.PENDING).size())
                .approvedReports((long) reportRepository.findByStatus(ReportStatus.APPROVED).size())
                .rejectedReports((long) reportRepository.findByStatus(ReportStatus.REJECTED).size())
                .build();
    }
}
