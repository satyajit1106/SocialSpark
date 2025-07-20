package com.hashedin.huSpark.service;

import com.hashedin.huSpark.dto.request.RegisterUserRequest;
import com.hashedin.huSpark.dto.response.PostResponse;
import com.hashedin.huSpark.dto.response.UserResponse;
import lombok.Data;

import java.util.List;

public interface AdminService {
    UserResponse createAdmin(RegisterUserRequest request);

    List<UserResponse> getAllUsers();

    Long getUserCount();

    Long getActiveUserCount();

    List<PostResponse> getAllPosts();

    Long getPostCount();

    Long getActivePostCount();
}
