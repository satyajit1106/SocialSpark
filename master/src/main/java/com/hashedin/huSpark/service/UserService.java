package com.hashedin.huSpark.service;

import com.hashedin.huSpark.dto.request.*;
import com.hashedin.huSpark.dto.response.FollowResponse;
import com.hashedin.huSpark.dto.response.LoginResponse;
import com.hashedin.huSpark.dto.response.UserResponse;
import com.hashedin.huSpark.entity.Post;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
    UserResponse registerUser(RegisterUserRequest request);

    LoginResponse loginUser(LoginRequest request);

    FollowResponse followUser(Long followerId, Long followeeId);

    void unfollowUser(Long followerId, Long followeeId);

    UserResponse updateVisibility();

    List<Post> getPostsOfUsers(Long userId);

    List<Post> getMyPosts();

    UserResponse getProfile(Long userId);

    void deactivate();

    UserResponse updateProfile(UpdateProfileRequest request);

    UserResponse changePassword(PasswordChangeRequest passwordChangeRequest);

    void bulkUploadUsers(MultipartFile file) throws IOException;
}
