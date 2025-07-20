package com.hashedin.huSpark.repository;

import com.hashedin.huSpark.entity.UserFollower;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserFollowerRepository extends JpaRepository<UserFollower, Long> {
    Optional<UserFollower> findByUserIdAndFollowerId(Long userId, Long followerId);
}
