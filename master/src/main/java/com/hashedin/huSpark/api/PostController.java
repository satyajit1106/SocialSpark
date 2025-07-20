package com.hashedin.huSpark.api;

import com.hashedin.huSpark.dto.request.AddCommentRequest;
import com.hashedin.huSpark.dto.request.PostRequest;
import com.hashedin.huSpark.dto.request.PostUpdateRequest;
import com.hashedin.huSpark.dto.request.ReportPostRequest;
import com.hashedin.huSpark.dto.response.*;
import com.hashedin.huSpark.entity.Comment;
import com.hashedin.huSpark.entity.Post;
import com.hashedin.huSpark.entity.PostReport;
import com.hashedin.huSpark.service.CommentService;
import com.hashedin.huSpark.service.PostReportService;
import com.hashedin.huSpark.service.PostService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/posts")
@AllArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;
    private final CommentService commentService;
    private final PostReportService reportService;

    private String saveImage(MultipartFile file) throws IOException {
        String uploadDir = "uploads/";
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = LocalDate.now().toString() + "_" + Objects.requireNonNull(file.getOriginalFilename()).replace(" ", "");
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filePath.toString();
    }

    @PostMapping("")
    public ResponseEntity<ApiResponse<PostResponse>> createPost(@RequestPart PostRequest postRequest, @RequestPart(required = false) MultipartFile image) throws IOException {
        String filePath = saveImage(image);
        ApiResponse<PostResponse> response = new ApiResponse<>(201, "Post created successfully", postService.createPost(postRequest, filePath));
        log.info("Post created successfully!");
        return ResponseEntity.status(201).body(response);
    }

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<ApiResponse<Object>> deletePost(@PathVariable Long postId) {
        postService.deletePostById(postId);
        ApiResponse<Object> response = new ApiResponse<>(200, "Post deleted successfully", null);
        log.info("Post deleted successfully.");
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping("/like/{postId}")
    public ResponseEntity<ApiResponse<?>> likePost(@PathVariable Long postId) {
        PostResponse postResponse = postService.likePost(postId);

        ApiResponse<PostResponse> response;

        if(postResponse == null) {
            response = new ApiResponse<>(200, "Post unliked successfully.", null);
        } else {
            response = new ApiResponse<>(200, "Post liked successfully.", postResponse);
        }

        log.info("Post like toggled.");
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/view/{postId}")
    public ResponseEntity<ApiResponse<PostResponse>> getPost(@PathVariable Long postId) {
        ApiResponse<PostResponse> response = new ApiResponse<>(200, "Post fetched successfully.", postService.getPost(postId));
        log.info("Post viewed successfully.");
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<List<CommentResponse>>>  getComments(@PathVariable Long postId) {
        ApiResponse<List<CommentResponse>> response = new ApiResponse<>(200, "Post comments fetched successfully.", commentService.getPostComments(postId));
        log.info("Comments retrieved successfully.");
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping("/{postId}/comments/add")
    public ResponseEntity<ApiResponse<CommentResponse>> addComment(@PathVariable Long postId, @RequestBody AddCommentRequest request) {
        ApiResponse<CommentResponse> response = new ApiResponse<>(200, "Comment added successfully.", commentService.addComment(postId, request.getText()));
        log.info("Comment added successfully.");
        return ResponseEntity.status(200).body(response);
    }

    @DeleteMapping("/{postId}/comments/delete/{commentId}")
    public ResponseEntity<ApiResponse<Object>> removeComment(@PathVariable Long postId, @PathVariable Long commentId) {
        commentService.removeComment(commentId, postId);
        ApiResponse<Object> response = new ApiResponse<>(200, "Comment deleted successfully.", null);
        log.info("Comment deleted successfully.");
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/{postId}/metrics")
    public ResponseEntity<ApiResponse<PostMetricsResponse>> getPostMetrics(@PathVariable Long postId) {
        ApiResponse<PostMetricsResponse> response = new ApiResponse<>(200, "Metrics fetched successfully.", postService.getMetrics(postId));
        log.info("Metrics fetched successfully.");
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping("/{postId}/share")
    public ResponseEntity<ApiResponse<PostShareResponse>> sharePost(@PathVariable Long postId) {
        ApiResponse<PostShareResponse> response = new ApiResponse<>(201, "Post shared successfully.", postService.sharePost(postId));
        log.info("Post shared successfully.");
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/{postId}/report")
    public ResponseEntity<ApiResponse<PostReport>> reportPost(@PathVariable Long postId, @RequestBody ReportPostRequest request) {
        ApiResponse<PostReport> response = new ApiResponse<>(201, "Post reported successfully.", reportService.reportPost(postId, request.getReason()));
        log.info("Post reported successfully.");
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/update/{postId}")
    public ResponseEntity<ApiResponse<PostResponse>> updatePost(@RequestPart PostUpdateRequest updateRequest, @RequestPart(required = false) MultipartFile image) throws IOException {
        String filePath = saveImage(image);

        ApiResponse<PostResponse> response = new ApiResponse<>(200, "Post updated successfully.", postService.updatePost(updateRequest, filePath));
        return ResponseEntity.status(200).body(response);
    }
}
