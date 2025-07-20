package com.hashedin.huSpark.service.impl;

import com.hashedin.huSpark.dto.request.*;
import com.hashedin.huSpark.dto.response.FollowResponse;
import com.hashedin.huSpark.dto.response.LoginResponse;
import com.hashedin.huSpark.dto.response.UserResponse;
import com.hashedin.huSpark.entity.*;
import com.hashedin.huSpark.repository.PostRepository;
import com.hashedin.huSpark.repository.UserFollowerRepository;
import com.hashedin.huSpark.repository.UserRepository;
import com.hashedin.huSpark.service.UserService;
import com.hashedin.huSpark.util.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserFollowerRepository userFollowerRepository;
    private final PostRepository postRepository;

    private List<User> mapCSVToUsers(MultipartFile file) throws IOException {
        List<User> users = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            for(CSVRecord record : csvParser) {
                users.add(User
                        .builder()
                                .name(record.get("name"))
                                .email(record.get("email"))
                                .password(passwordEncoder.encode(record.get("password")))
                                .dob(LocalDate.parse(record.get("dob")))
                                .role(Role.USER)
                                .visibility(Visibility.PUBLIC)
                                .lastPasswordUpdated(LocalDateTime.now())
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                        .build());
            }
        }

        return users;
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse
                .builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .visibility(user.getVisibility())
                .role(user.getRole())
                .dob(user.getDob())
                .build();
    }

    /**
     * @param request RegisterUserRequest
     * @return UserResponse
     */
    @Override
    public UserResponse registerUser(RegisterUserRequest request) {
        if(userRepository.findByEmailIgnoreCase(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with the provided details already exists.");
        }

        // Role role = request.getEmail().endsWith("@socio.com") ? Role.ADMIN : Role.USER;

        User user = User
                .builder()
                .email(request.getEmail())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .dob(request.getDob())
                .role(Role.USER)
                .isActive(true)
                .visibility(Visibility.PUBLIC)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .lastPasswordUpdated(LocalDateTime.now())
                .build();

        return mapToUserResponse(userRepository.save(user));
    }

    /**
     * @param request LoginRequest
     * @return LoginResponse
     */
    @Override
    public LoginResponse loginUser(LoginRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.getEmail()).orElseThrow(() -> new IllegalArgumentException("Invalid credentials."));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials.");
        }

        if(!user.isPasswordValid()) {
            throw new IllegalStateException("Password expired. Please reset your password.");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        return new LoginResponse(token);
    }

    /**
     * @param followerId whom to follow
     * @param followeeId who is following
     * @return FollowResponse
     */
    @Override
    public FollowResponse followUser(Long followerId, Long followeeId) {
        if(Objects.equals(followerId, followeeId)) {
            throw new IllegalArgumentException("You cannot follow yourself!");
        }
        User follower = userRepository.findById(followerId).orElseThrow(() -> new IllegalArgumentException("Follower not found!"));

        User followee = userRepository.findById(followeeId).orElseThrow(() -> new IllegalArgumentException("Followee not found!"));

        userFollowerRepository.save(UserFollower
                        .builder()
                        .user(followee)
                        .follower(follower)
                        .followedAt(LocalDateTime.now())
                        .build());

        return new FollowResponse(followerId, followeeId, LocalDateTime.now());
    }

    /**
     * @param followerId FollowerId
     * @param followeeId followeeId
     */
    @Override
    public void unfollowUser(Long followerId, Long followeeId) {
        if(followerId.equals(followeeId)) {
            throw new IllegalArgumentException("You cannot unfollow yourself.");
        }

        Optional<UserFollower> userFollower = userFollowerRepository.findByUserIdAndFollowerId(followerId, followeeId);

        if(userFollower.isEmpty()) {
            throw new IllegalArgumentException("You are not following this user. Unable to unfollow");
        }

        userFollowerRepository.delete(userFollower.get());
    }

    /**
     * @param
     * @return UserResponse
     */
    @Override
    public UserResponse updateVisibility() {
        Optional<User> user = userRepository.findByEmailIgnoreCase(SecurityContextHolder.getContext().getAuthentication().getName());

        if(user.isEmpty()) {
            throw new IllegalArgumentException("User not found.");
        }

        if(!user.get().isActive()) {
            throw new RuntimeException("This account was deactivated recently.");
        }

        User existingUser = user.get();

        existingUser.setVisibility(existingUser.getVisibility().toString().equalsIgnoreCase("private") ? Visibility.PUBLIC : Visibility.PRIVATE);
        userRepository.save(existingUser);
        return UserResponse
                .builder()
                .id(existingUser.getId())
                .name(existingUser.getName())
                .email(existingUser.getEmail())
                .dob(existingUser.getDob())
                .role(existingUser.getRole())
                .visibility(existingUser.getVisibility())
                .build();
    }

    /**
     * @param userId Long
     * @return List<Post>
     */
    @Override
    public List<Post> getPostsOfUsers(Long userId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<User> requester = userRepository.findByEmailIgnoreCase(email);

        if(requester.isEmpty()) {
            throw new EntityNotFoundException("Requesting account does not exist.");
        }

        Optional<User> user = userRepository.findById(userId);

        if(user.isEmpty()) {
            throw new EntityNotFoundException("User does not exist.");
        }

        if(requester.get().getId().equals(user.get().getId())) {
            throw new RuntimeException("Trying to fetch your own posts? Make a request on /api/users/me/posts instead.");
        }

        if(!requester.get().isActive()) {
            throw new RuntimeException("This account was deactivated recently.");
        }

        if(user.get().getVisibility().toString().equalsIgnoreCase("private")) {
            Optional<UserFollower> userFollower = userFollowerRepository.findByUserIdAndFollowerId(userId, requester.get().getId());

            if(userFollower.isEmpty()) {
                throw new IllegalArgumentException("This account is private. Follow them to see their posts.");
            }

        }
        return postRepository.findByUserId(userId).stream().filter(post -> !post.isDeleted()).collect(Collectors.toList());
    }

    /**
     * @return List<Post>
     */
    @Override
    public List<Post> getMyPosts() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<User> requester = userRepository.findByEmailIgnoreCase(email);

        if(requester.isEmpty()) {
            throw new EntityNotFoundException("User account does not exist.");
        }

        if(!requester.get().isActive()) {
            throw new RuntimeException("This account was deactivated recently.");
        }

        return postRepository.findByUserId(requester.get().getId()).stream().filter(post -> !post.isDeleted()).collect(Collectors.toList());
    }

    /**
     * @param userId
     * @return
     */
    @Override
    public UserResponse getProfile(Long userId) {
       Optional<User> user = userRepository.findById(userId);

       if(user.isEmpty()) {
           throw new EntityNotFoundException("User does not exist.");
       }

       if(!user.get().isActive()) {
           throw new RuntimeException("This account was deactivated recently.");
       }

       User existingUser = user.get();

        return UserResponse
                .builder()
                .id(existingUser.getId())
                .name(existingUser.getName())
                .email(existingUser.getEmail())
                .dob(existingUser.getDob())
                .role(existingUser.getRole())
                .visibility(existingUser.getVisibility())
                .build();
    }

    /**
     *
     */
    @Override
    public void deactivate() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<User> user = userRepository.findByEmailIgnoreCase(email);

        if(user.isEmpty()) {
            throw new EntityNotFoundException("User does not exist.");
        }

        User existingUser = user.get();

        existingUser.setActive(!existingUser.isActive());
        userRepository.save(existingUser);
    }

    /**
     * @param request
     * @return
     */
    @Override
    public UserResponse updateProfile(UpdateProfileRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<User> user = userRepository.findByEmailIgnoreCase(email);

        if(user.isEmpty()) {
            throw new EntityNotFoundException("User account does not exist.");
        }

        User existingUser = user.get();

        if(!existingUser.isActive()) {
            throw new RuntimeException("User account was deleted recently. Unable to update");
        }

        existingUser.setName(request.getName());
        return mapToUserResponse(userRepository.save(existingUser));
    }

    /**
     * @param passwordChangeRequest
     * @return
     */
    @Override
    public UserResponse changePassword(PasswordChangeRequest passwordChangeRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<User> user = userRepository.findByEmailIgnoreCase(email);

        if(user.isEmpty()) {
            throw new EntityNotFoundException("User does not exist.");
        }

        User existingUser = user.get();

        if(passwordEncoder.matches(passwordChangeRequest.getOldPassword(), existingUser.getPassword())) {
            existingUser.setPassword(passwordEncoder.encode(passwordChangeRequest.getNewPassword()));
            existingUser.setLastPasswordUpdated(LocalDateTime.now());
            return mapToUserResponse(userRepository.save(existingUser));
        }

        throw new RuntimeException("Invalid credentials.");
    }

    /**
     * @param file
     */
    @Override
    public void bulkUploadUsers(MultipartFile file) throws IOException {
        if(!Objects.requireNonNull(file.getOriginalFilename()).endsWith(".csv")) {
            throw new RuntimeException("Only csv files are supported in bulk uploads as of now.");
        }

        List<User> users = mapCSVToUsers(file);
        userRepository.saveAll(users);
    }
}
