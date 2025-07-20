package com.hashedin.huSpark.service;

import com.hashedin.huSpark.entity.UserGroup;

import java.util.List;

public interface UserGroupService {
    UserGroup createGroup(String name);

    List<UserGroup> getAllGroups();

    UserGroup getGroupById(Long groupId);
}
