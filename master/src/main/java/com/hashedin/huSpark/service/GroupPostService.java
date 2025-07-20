package com.hashedin.huSpark.service;

import com.hashedin.huSpark.entity.GroupPost;

import java.util.List;

public interface GroupPostService {
    GroupPost createPost(Long groupId, String text);

    List<GroupPost> getGroupPosts(Long groupId);
}
