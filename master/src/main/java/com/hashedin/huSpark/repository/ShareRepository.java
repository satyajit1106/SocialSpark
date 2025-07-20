package com.hashedin.huSpark.repository;

import com.hashedin.huSpark.entity.Share;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShareRepository extends JpaRepository<Share, Long> {
    Optional<Share> findByUserIdAndPostId(Long userId, Long postId);

    Long countByPostId(Long postId);
}
