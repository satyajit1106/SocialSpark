package com.hashedin.huSpark.service.impl;

import com.hashedin.huSpark.entity.GroupMember;
import com.hashedin.huSpark.entity.User;
import com.hashedin.huSpark.entity.UserGroup;
import com.hashedin.huSpark.repository.GroupMemberRepository;
import com.hashedin.huSpark.repository.UserGroupRepository;
import com.hashedin.huSpark.repository.UserRepository;
import com.hashedin.huSpark.service.GroupMemberService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class GroupMemberServiceImpl implements GroupMemberService {
    private final UserGroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupMemberRepository memberRepository;

    /**
     * @param groupId
     * @return
     */
    @Override
    public GroupMember joinGroup(Long groupId) {
        Optional<UserGroup> group = groupRepository.findById(groupId);

        if(group.isEmpty()) {
            throw new EntityNotFoundException("Group does not exist.");
        }

        UserGroup existingGroup = group.get();

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<User> user = userRepository.findByEmailIgnoreCase(email);

        if(user.isEmpty()) {
            throw new EntityNotFoundException("User does not exist.");
        }

        User existingUser = user.get();

        if(!existingUser.isActive()) {
            throw new RuntimeException("User account was deleted recently.");
        }

        if(memberRepository.existsByGroupIdAndUserId(groupId, existingUser.getId())) {
            throw new RuntimeException("User is already a member of the group.");
        }

        GroupMember member = GroupMember
                .builder()
                .user(existingUser)
                .group(existingGroup)
                .joinedAt(LocalDateTime.now())
                .build();

        return memberRepository.save(member);
    }

    /**
     * @param groupId
     * @return
     */
    @Override
    public List<GroupMember> getGroupMembers(Long groupId) {
        return memberRepository.findByGroupId(groupId);
    }

    /**
     * @param groupId
     */
    @Override
    public void leaveGroup(Long groupId) {
        List<GroupMember> members = memberRepository.findByGroupId(groupId);

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<User> user = userRepository.findByEmailIgnoreCase(email);

        if(user.isEmpty()) {
            throw new EntityNotFoundException("User does not exist.");
        }

        User existingUser = user.get();

        Optional<GroupMember> groupMember = members.stream().filter(member -> member.getUser().getId().equals(existingUser.getId())).findFirst();

        if(groupMember.isEmpty()) {
            throw new EntityNotFoundException("You are not a member of this group.");
        }

        memberRepository.delete(groupMember.get());
    }
}
