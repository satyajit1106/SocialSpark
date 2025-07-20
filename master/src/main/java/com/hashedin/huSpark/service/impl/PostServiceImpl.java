package com.hashedin.huSpark.service.impl;

import com.hashedin.huSpark.dto.request.PostRequest;
import com.hashedin.huSpark.dto.request.PostUpdateRequest;
import com.hashedin.huSpark.dto.response.PostMetricsResponse;
import com.hashedin.huSpark.dto.response.PostResponse;
import com.hashedin.huSpark.dto.response.PostShareResponse;
import com.hashedin.huSpark.dto.response.UnlikeResponse;
import com.hashedin.huSpark.entity.*;
import com.hashedin.huSpark.repository.*;
import com.hashedin.huSpark.service.PostService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final UserFollowerRepository followerRepository;
    private final ShareRepository shareRepository;

    PostResponse EntityToDTO(Post post) {
        return PostResponse
                .builder()
                .id(post.getId())
                .text(post.getText())
                .filePath(post.getFilePath().isEmpty() ? null : post.getFilePath())
                .user(post.getUser())
                .build();
    }

    PostResponse postLikeToDTO(PostLike postLike) {
        return PostResponse
                .builder()
                .id(postLike.getPost().getId())
                .text(postLike.getPost().getText())
                .filePath(postLike.getPost().getFilePath().isEmpty() ? null : postLike.getPost().getFilePath())
                .user(postLike.getUser())
                .build();
    }

    PostShareResponse postShareToDTO(Share share) {
        return PostShareResponse
                .builder()
                .id(share.getId())
                .post(share.getPost())
                .user(share.getUser())
                .build();
    }

    /**
     * @param postRequest PostRequest
     * @param filePath String
     * @return PostResponse
     */
    @Override
    public PostResponse createPost(PostRequest postRequest, String filePath) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<User> existingUser = userRepository.findByEmailIgnoreCase(email);

        if(existingUser.isEmpty()) {
            throw new EntityNotFoundException("User does not exist.");
        }

        User user = existingUser.get();

        Post createdPost = Post
                .builder()
                .text(postRequest.getText())
                .filePath(filePath.isEmpty() ? "" : filePath)
                .user(user)
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return EntityToDTO(postRepository.save(createdPost));
    }

    /**
     * @param postId Long
     */
    @Override
    public void deletePostById(Long postId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<User> user = userRepository.findByEmailIgnoreCase(email);

        if(user.isEmpty()) {
            throw new EntityNotFoundException("User does not exist");
        }

        User existingUser = user.get();

        if(!existingUser.isActive()) {
            throw new RuntimeException("This account was deactivated recently.");
        }

        Optional<Post> post = postRepository.findById(postId);

        if(post.isEmpty()) {
            throw new EntityNotFoundException("Post does not exist.");
        }

        if(post.get().isDeleted()) {
            throw new RuntimeException("Post already deleted. Cannot delete again!");
        }

        Post existingPost = post.get();

        User postOwner = existingPost.getUser();

        if(!Objects.equals(postOwner.getId(), existingUser.getId())) {
            throw new RuntimeException("You are not the owner of this post. Only owners can delete posts.");
        }

        existingPost.setDeleted(true);

        postRepository.save(existingPost);
    }

    /**
     * @param postId
     * @return
     */
    @Override
    public PostResponse likePost(Long postId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<User> user = userRepository.findByEmailIgnoreCase(email);

        if(user.isEmpty()) {
            throw new EntityNotFoundException("User does not exist");
        }

        User existingUser = user.get();

        if(!existingUser.isActive()) {
            throw new RuntimeException("This account was deactivated recently.");
        }

        Optional<PostLike> existingPostLike = likeRepository.findByPostIdAndUserId(postId, existingUser.getId());

        if(existingPostLike.isPresent()) {
            likeRepository.deleteById(existingPostLike.get().getId());
            return null;
        }

        Optional<Post> post = postRepository.findById(postId);

        if(post.isEmpty()) {
            throw new EntityNotFoundException("Post does not exist.");
        }

        if(post.get().isDeleted()) {
            throw new RuntimeException("You cannot like a deleted post!");
        }

        Post existingPost = post.get();

        PostLike postLike = PostLike
                .builder()
                .user(existingUser)
                .post(existingPost)
                .likedAt(LocalDateTime.now())
                .build();

        return postLikeToDTO(likeRepository.save(postLike));
    }

    /**
     * @param postId
     * @return
     */
    @Override
    public PostResponse getPost(Long postId) {
        Optional<Post> post = postRepository.findById(postId);

        if(post.isEmpty()) {
            throw new EntityNotFoundException("Post not found!");
        }

        Post existingPost = post.get();

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<User> user = userRepository.findByEmailIgnoreCase(email);

        if(user.isEmpty()) {
            throw new EntityNotFoundException("User not found!");
        }

        if(!user.get().isActive()) {
            throw new RuntimeException("User account was deactivated recently.");
        }

        if(existingPost.isDeleted()) {
            throw new RuntimeException("This post was deleted.");
        }

        if(existingPost.getUser().getVisibility().toString().equalsIgnoreCase("private")) {
            Optional<UserFollower> userFollower = followerRepository.findByUserIdAndFollowerId(existingPost.getUser().getId(), user.get().getId());

            if(userFollower.isEmpty()) {
                throw new RuntimeException("User's account is private. You need to follow them to view this post.");
            }
        }

        return EntityToDTO(post.get());
    }

    /**
     * @param postId
     * @return
     */
    @Override
    public PostMetricsResponse getMetrics(Long postId) {
        Optional<Post> post = postRepository.findById(postId);

        if(post.isEmpty()) {
            throw new EntityNotFoundException("Post with the provided ID does not exist.");
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<User> user = userRepository.findByEmailIgnoreCase(email);

        if(user.isEmpty()) {
            throw new EntityNotFoundException("User not found.");
        }

        if(!user.get().isActive()) {
            throw new RuntimeException("User account was deactivated recently.");
        }

        if(post.get().isDeleted()) {
            throw new RuntimeException("This post was deleted.");
        }

        return PostMetricsResponse
                .builder()
                .id(postId)
                .likes(likeRepository.countByPostId(postId))
                .comments(commentRepository.countByPostId(postId))
                .shares(shareRepository.countByPostId(postId))
                .build();
    }

    /**
     * @param postId
     * @return
     */
    @Override
    public PostShareResponse sharePost(Long postId) {
        Optional<Post> post = postRepository.findById(postId);

        if(post.isEmpty()) {
            throw new EntityNotFoundException("Post not found!");
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<User> user = userRepository.findByEmailIgnoreCase(email);

        if(user.isEmpty()) {
            throw new EntityNotFoundException("User not found!");
        }

        Share sharedPost = Share
                .builder()
                .post(post.get())
                .user(user.get())
                .sharedAt(LocalDateTime.now())
                .build();

        return postShareToDTO(shareRepository.save(sharedPost));
    }

    /**
     * @param postUpdateRequest
     * @param filePath
     * @return
     */
    @Override
    public PostResponse updatePost(PostUpdateRequest postUpdateRequest, String filePath) {
        Optional<Post> post = postRepository.findById(postUpdateRequest.getPostId());

        if(post.isEmpty()) {
            throw new EntityNotFoundException("Post does not exist.");
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<User> user = userRepository.findByEmailIgnoreCase(email);

        if(user.isEmpty()) {
            throw new EntityNotFoundException("User does not exist.");
        }

        User existingUser = user.get();

        Post existingPost = post.get();

        if(!Objects.equals(existingPost.getUser().getId(), existingUser.getId())) {
            throw new RuntimeException("You cannot update someone else's post.");
        }

        if(postUpdateRequest.getText() != null) {
            existingPost.setText(postUpdateRequest.getText());
        }

        if(filePath != null && !filePath.isEmpty()) {
            existingPost.setFilePath(filePath);
        }

        existingPost.setUpdatedAt(LocalDateTime.now());
        return EntityToDTO(postRepository.save(existingPost));
    }
}
