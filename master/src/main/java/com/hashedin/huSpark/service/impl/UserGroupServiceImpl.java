package com.hashedin.huSpark.service.impl;

import com.hashedin.huSpark.entity.User;
import com.hashedin.huSpark.entity.UserGroup;
import com.hashedin.huSpark.repository.UserGroupRepository;
import com.hashedin.huSpark.repository.UserRepository;
import com.hashedin.huSpark.service.UserGroupService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserGroupServiceImpl implements UserGroupService {
    private final UserRepository userRepository;
    private final UserGroupRepository groupRepository;

    /**
     * @param name
     * @return
     */
    @Override
    public UserGroup createGroup(String name) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<User> user = userRepository.findByEmailIgnoreCase(email);

        if(user.isEmpty()) {
            throw new EntityNotFoundException("User account does not exist.");
        }

        User existingUser = user.get();

        UserGroup group = UserGroup
                .builder()
                .name(name)
                .admin(existingUser)
                .isPrivate(false)
                .createdAt(LocalDateTime.now())
                .build();

        return groupRepository.save(group);
    }

    /**
     * @return
     */
    @Override
    public List<UserGroup> getAllGroups() {
        return groupRepository.findAll();
    }

    /**
     * @param groupId
     * @return
     */
    @Override
    public UserGroup getGroupById(Long groupId) {
        Optional<UserGroup> group = groupRepository.findById(groupId);

        if(group.isEmpty()) {
            throw new EntityNotFoundException("Group does not exist.");
        }

        return group.get();
    }
}
