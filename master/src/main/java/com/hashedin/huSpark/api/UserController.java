package com.hashedin.huSpark.api;

import com.hashedin.huSpark.dto.request.*;
import com.hashedin.huSpark.dto.response.ApiResponse;
import com.hashedin.huSpark.dto.response.FollowResponse;
import com.hashedin.huSpark.dto.response.LoginResponse;
import com.hashedin.huSpark.dto.response.UserResponse;
import com.hashedin.huSpark.entity.Post;
import com.hashedin.huSpark.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterUserRequest request) {
        ApiResponse<UserResponse> response = new ApiResponse<>(201, "User account created successfully.", userService.registerUser(request));
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        ApiResponse<LoginResponse> response = new ApiResponse<>(200, "User logged in.", userService.loginUser(request));
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping("/follow/{userId}")
    public ResponseEntity<ApiResponse<FollowResponse>> followUser(@PathVariable Long userId, @RequestParam Long followerId) {
        ApiResponse<FollowResponse> response = new ApiResponse<>(200, "User followed successfully.", userService.followUser(followerId, userId));
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping("/unfollow/{userId}")
    public ResponseEntity<ApiResponse<Object>> unfollowUser(@PathVariable Long userId, @RequestParam Long followerId) {
        userService.unfollowUser(userId, followerId);
        ApiResponse<Object> response = new ApiResponse<>(200, "User unfollowed successfully.", null);
        return ResponseEntity.status(200).body(response);
    }

    @PutMapping("/update-visibility")
    public ResponseEntity<ApiResponse<UserResponse>> updateVisibility() {
        ApiResponse<UserResponse> response = new ApiResponse<>(200, "Visibility updated successfully.", userService.updateVisibility());
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/{userId}/posts")
    public ResponseEntity<ApiResponse<List<Post>>> getUserPosts(@PathVariable Long userId) {
        ApiResponse<List<Post>> response = new ApiResponse<>(200, "Provided user's posts fetched successfully", userService.getPostsOfUsers(userId));
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/me/posts")
    public ResponseEntity<ApiResponse<List<Post>>> getMyPosts() {
        ApiResponse<List<Post>> response = new ApiResponse<>(200, "User's posts fetched successfully", userService.getMyPosts());
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> viewProfile(@PathVariable Long userId) {
        ApiResponse<UserResponse> response = new ApiResponse<>(200, "Profile fetched successfully.", userService.getProfile(userId));
        return ResponseEntity.status(200).body(response);
    }

    @PutMapping("/deactivate")
    public ResponseEntity<ApiResponse<Object>> deactivateProfile() {
        userService.deactivate();
        ApiResponse<Object> response = new ApiResponse<>(200, "Toggled account deactivation successfully.", null);
        return ResponseEntity.status(200).body(response);
    }

    @PatchMapping("/update-name")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(@RequestBody UpdateProfileRequest request) {
        ApiResponse<UserResponse> response = new ApiResponse<>(200, "User profile updated successfully.", userService.updateProfile(request));
        return ResponseEntity.status(200).body(response);
    }

    @PutMapping("/reset-password")
    public ResponseEntity<ApiResponse<UserResponse>> changePassword(@RequestBody PasswordChangeRequest passwordChangeRequest) {
        ApiResponse<UserResponse> response = new ApiResponse<>(200, "Password changed successfully.", userService.changePassword(passwordChangeRequest));
        return ResponseEntity.status(200).body(response);
    }
}
