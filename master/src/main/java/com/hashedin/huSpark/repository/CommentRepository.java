package com.hashedin.huSpark.repository;

import com.hashedin.huSpark.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPostId(Long postId);

    Optional<Comment> findByIdAndPostId(Long commentId, Long postId);

    Long countByPostId(Long postId);
}
