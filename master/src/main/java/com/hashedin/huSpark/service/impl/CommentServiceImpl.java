package com.hashedin.huSpark.service.impl;

import com.hashedin.huSpark.dto.response.CommentResponse;
import com.hashedin.huSpark.dto.response.UserResponse;
import com.hashedin.huSpark.entity.Comment;
import com.hashedin.huSpark.entity.Post;
import com.hashedin.huSpark.entity.User;
import com.hashedin.huSpark.repository.CommentRepository;
import com.hashedin.huSpark.repository.PostRepository;
import com.hashedin.huSpark.repository.UserRepository;
import com.hashedin.huSpark.service.CommentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    CommentResponse EntityToDTO(Comment comment) {
        return CommentResponse
                .builder()
                .id(comment.getId())
                .text(comment.getText())
                .post(comment.getPost())
                .user(comment.getUser())
                .build();
    }

    /**
     * @param postId
     * @return
     */
    @Override
    public List<CommentResponse> getPostComments(Long postId) {
        return commentRepository.findAllByPostId(postId).stream().filter(comment -> !comment.isDeleted()).map(this::EntityToDTO).collect(Collectors.toList());
    }

    /**
     * @param postId
     * @param text
     * @return
     */
    @Override
    public CommentResponse addComment(Long postId, String text) {
        Optional<Post> post = postRepository.findById(postId);

        if(post.isEmpty()) {
            throw new EntityNotFoundException("Post not found.");
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<User> user = userRepository.findByEmailIgnoreCase(email);

        if(user.isEmpty()) {
            throw new EntityNotFoundException("User not found.");
        }

        Comment comment = Comment
                .builder()
                .text(text)
                .user(user.get())
                .post(post.get())
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .build();

        return EntityToDTO(commentRepository.save(comment));
    }

    /**
     * @param commentId
     * @param postId
     */
    @Override
    public void removeComment(Long commentId, Long postId) {
        Optional<Post> post = postRepository.findById(postId);

        if(post.isEmpty()) {
            throw new EntityNotFoundException("Post not found!");
        }

        Post existingPost = post.get();

        if(!Objects.equals(existingPost.getUser().getEmail(), SecurityContextHolder.getContext().getAuthentication().getName())) {
            throw new RuntimeException("You cannot delete someone else's comment!");
        }

        Optional<Comment> comment = commentRepository.findByIdAndPostId(commentId, postId);

        if(comment.isEmpty()) {
            throw new EntityNotFoundException("Comment does not exist on provided post!");
        }

        Comment existingComment = comment.get();
        existingComment.setDeleted(true);
        commentRepository.save(existingComment);
    }
}
