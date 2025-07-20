package com.hashedin.huSpark.service;

import com.hashedin.huSpark.entity.GroupMember;

import java.util.List;

public interface GroupMemberService {
    GroupMember joinGroup(Long groupId);

    List<GroupMember> getGroupMembers(Long groupId);

    void leaveGroup(Long groupId);
}
