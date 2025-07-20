package com.hashedin.huSpark.repository;

import com.hashedin.huSpark.entity.GroupPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupPostRepository extends JpaRepository<GroupPost, Long> {
    boolean existsByGroupIdAndUserId(Long groupId, Long userId);
}
