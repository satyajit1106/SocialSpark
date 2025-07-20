package com.hashedin.huSpark.api;

import com.hashedin.huSpark.dto.request.RegisterUserRequest;
import com.hashedin.huSpark.dto.response.ApiResponse;
import com.hashedin.huSpark.dto.response.PostResponse;
import com.hashedin.huSpark.dto.response.ReportMetricsResponse;
import com.hashedin.huSpark.dto.response.UserResponse;
import com.hashedin.huSpark.entity.PostReport;
import com.hashedin.huSpark.service.AdminService;
import com.hashedin.huSpark.service.PostReportService;
import com.hashedin.huSpark.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@AllArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final PostReportService reportService;
    private final UserService userService;

    /**
     * Tests if admin controller is working
     * @return String test response
     */
    @GetMapping("/test")
    public ResponseEntity<ApiResponse<String>> testAdmin() {
        ApiResponse<String> response = new ApiResponse<>(200, "Admin API hit!", "Only admins can see this");
        return ResponseEntity.status(200).body(response);
    }

    /**
     * Allows existing admins to create new ones.
     * @param request request body to register a new user
     * @return created user response
     */
    @Operation(summary = "Creates admin", description = "Allows an existing admin to create a new admin")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Admin creation API")
    @PostMapping("/new")
    public ResponseEntity<ApiResponse<UserResponse>> createAdmin(@RequestBody RegisterUserRequest request) {
        ApiResponse<UserResponse> response = new ApiResponse<>(201, "Admin account created successfully.", adminService.createAdmin(request));
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/get/users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        ApiResponse<List<UserResponse>> response = new ApiResponse<>(200, "Fetched all users successfully.", adminService.getAllUsers());
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/count/users")
    public ResponseEntity<ApiResponse<Long>> getUserCount() {
        ApiResponse<Long> response = new ApiResponse<>(200, "User count fetched successfully.", adminService.getUserCount());
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/count/users/active")
    public ResponseEntity<ApiResponse<Long>> getActiveUserCount() {
        ApiResponse<Long> response = new ApiResponse<>(200, "Active user count fetched successfully.", adminService.getActiveUserCount());
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/get/posts")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getAllPosts() {
        ApiResponse<List<PostResponse>> response = new ApiResponse<>(200, "Posts fetched successfully.", adminService.getAllPosts());
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/count/posts")
    public ResponseEntity<ApiResponse<Long>> getPostCount() {
        ApiResponse<Long> response = new ApiResponse<>(200, "Post count fetched successfully.", adminService.getPostCount());
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/count/posts/active")
    public ResponseEntity<ApiResponse<Long>> getActivePostCount() {
        ApiResponse<Long> response = new ApiResponse<>(200, "Active post count fetched successfully.", adminService.getActivePostCount());
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/reports/pending")
    public ResponseEntity<ApiResponse<List<PostReport>>> getPendingReports() {
        ApiResponse<List<PostReport>> response = new ApiResponse<>(200, "Pending post reports fetched successfully.", reportService.getPendingReports());
        return ResponseEntity.status(200).body(response);
    }

    @PutMapping("/reports/approve/{reportId}")
    public ResponseEntity<ApiResponse<Object>> approveReport(@PathVariable Long reportId) {
        reportService.approveReport(reportId);
        ApiResponse<Object> response = new ApiResponse<>(200, "Report approved successfully.", null);
        return ResponseEntity.status(200).body(response);
    }

    @PutMapping("/reports/reject/{reportId}")
    public ResponseEntity<ApiResponse<Object>> rejectReport(@PathVariable Long reportId) {
        reportService.rejectReport(reportId);
        ApiResponse<Object> response = new ApiResponse<>(200, "Report rejected successfully.", null);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/reports/metrics")
    public ResponseEntity<ApiResponse<ReportMetricsResponse>> getReportMetrics() {
        ApiResponse<ReportMetricsResponse> response = new ApiResponse<>(200, "Report metrics fetched successfully.", reportService.getMetrics());
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping("/bulk-upload")
    public ResponseEntity<ApiResponse<Object>> bulkUpload(@RequestPart MultipartFile file) throws IOException {
        userService.bulkUploadUsers(file);
        ApiResponse<Object> response = new ApiResponse<>(201, "Users created successfully.", null);
        return ResponseEntity.status(201).body(response);
    }
}
