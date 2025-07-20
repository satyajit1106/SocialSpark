package com.hashedin.huSpark.service.impl;

import com.hashedin.huSpark.dto.request.RegisterUserRequest;
import com.hashedin.huSpark.dto.response.PostResponse;
import com.hashedin.huSpark.dto.response.UserResponse;
import com.hashedin.huSpark.entity.Post;
import com.hashedin.huSpark.entity.Role;
import com.hashedin.huSpark.entity.User;
import com.hashedin.huSpark.entity.Visibility;
import com.hashedin.huSpark.repository.PostRepository;
import com.hashedin.huSpark.repository.UserRepository;
import com.hashedin.huSpark.service.AdminService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    PostResponse postToPostResponse(Post post) {
        return PostResponse
                .builder()
                .id(post.getId())
                .user(post.getUser())
                .text(post.getText())
                .filePath(post.getFilePath())
                .build();
    }

    UserResponse EntityToDTO(User user) {
        return UserResponse
                .builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .visibility(user.getVisibility())
                .role(user.getRole())
                .dob(user.getDob())
                .build();
    }

    /**
     * @param request
     * @return
     */
    @Override
    public UserResponse createAdmin(RegisterUserRequest request) {
        User createdAdminUser = User
                .builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .dob(request.getDob())
                .role(Role.ADMIN)
                .visibility(Visibility.PUBLIC)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .lastPasswordUpdated(LocalDateTime.now())
                .build();

        return EntityToDTO(userRepository.save(createdAdminUser));
    }

    /**
     * @return
     */
    @Override
    public List<UserResponse> getAllUsers() {
       return userRepository.findAll().stream().map(this::EntityToDTO).toList();
    }

    /**
     * @return
     */
    @Override
    public Long getUserCount() {
       return userRepository.count();
    }

    /**
     * @return
     */
    @Override
    public Long getActiveUserCount() {
        return userRepository.findAll().stream().filter(User::isActive).count();
    }

    /**
     * @return
     */
    @Override
    public List<PostResponse> getAllPosts() {
        return postRepository.findAll().stream().map(this::postToPostResponse).toList();
    }

    /**
     * @return
     */
    @Override
    public Long getPostCount() {
        return postRepository.count();
    }

    /**
     * @return
     */
    @Override
    public Long getActivePostCount() {
        return postRepository.findAll().stream().filter(post -> !post.isDeleted()).count();
    }
}
