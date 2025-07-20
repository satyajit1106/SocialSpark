package com.hashedin.huSpark.service.impl;

import com.hashedin.huSpark.entity.GroupPost;
import com.hashedin.huSpark.entity.User;
import com.hashedin.huSpark.entity.UserGroup;
import com.hashedin.huSpark.repository.GroupMemberRepository;
import com.hashedin.huSpark.repository.GroupPostRepository;
import com.hashedin.huSpark.repository.UserGroupRepository;
import com.hashedin.huSpark.repository.UserRepository;
import com.hashedin.huSpark.service.GroupPostService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class GroupPostServiceImpl implements GroupPostService {
    private final UserRepository userRepository;
    private final UserGroupRepository groupRepository;
    private final GroupMemberRepository memberRepository;
    private final GroupPostRepository groupPostRepository;

    /**
     * @param groupId
     * @param text
     * @return
     */
    @Override
    public GroupPost createPost(Long groupId, String text) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<User> user = userRepository.findByEmailIgnoreCase(email);

        if(user.isEmpty()) {
            throw new EntityNotFoundException("User does not exist.");
        }

        User existingUser = user.get();

        Optional<UserGroup> group = groupRepository.findById(groupId);

        if(group.isEmpty()) {
            throw new EntityNotFoundException("Group does not exist.");
        }

        if(!memberRepository.existsByGroupIdAndUserId(groupId, existingUser.getId())) {
            throw new RuntimeException("Only members of this group can post.");
        }

        GroupPost post = GroupPost
                .builder()
                .group(group.get())
                .user(existingUser)
                .text(text)
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return groupPostRepository.save(post);
    }

    /**
     * @param groupId
     * @return
     */
    @Override
    public List<GroupPost> getGroupPosts(Long groupId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<User> user = userRepository.findByEmailIgnoreCase(email);

        if(user.isEmpty()) {
            throw new EntityNotFoundException("User does not exist.");
        }

        User existingUser = user.get();

        Optional<UserGroup> group = groupRepository.findById(groupId);

        if(group.isEmpty()) {
            throw new EntityNotFoundException("Group does not exist.");
        }

        UserGroup existingGroup = group.get();

        if(existingGroup.isPrivate()) {
            if(!memberRepository.existsByGroupIdAndUserId(groupId, existingUser.getId())) {
                throw new RuntimeException("Only group members can view posts.");
            }

            return groupPostRepository.findAll();
        }

        return groupPostRepository.findAll();
    }
}
