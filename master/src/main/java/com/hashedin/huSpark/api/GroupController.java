package com.hashedin.huSpark.api;

import com.hashedin.huSpark.dto.request.GroupCreateRequest;
import com.hashedin.huSpark.dto.request.GroupPostRequest;
import com.hashedin.huSpark.dto.response.ApiResponse;
import com.hashedin.huSpark.entity.GroupMember;
import com.hashedin.huSpark.entity.GroupPost;
import com.hashedin.huSpark.entity.UserGroup;
import com.hashedin.huSpark.service.GroupMemberService;
import com.hashedin.huSpark.service.GroupPostService;
import com.hashedin.huSpark.service.UserGroupService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@AllArgsConstructor
public class GroupController {
    private final UserGroupService groupService;
    private final GroupMemberService memberService;
    private final GroupPostService groupPostService;

    @PostMapping("")
    public ResponseEntity<ApiResponse<UserGroup>> createGroup(@RequestBody GroupCreateRequest request) {
        ApiResponse<UserGroup> response = new ApiResponse<>(201, "Group created successfully.", groupService.createGroup(request.getName()));
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<UserGroup>>> getAllGroups() {
        ApiResponse<List<UserGroup>> response = new ApiResponse<>(200, "Groups fetched successfully.", groupService.getAllGroups());
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<ApiResponse<UserGroup>> getGroup(@PathVariable Long groupId) {
        ApiResponse<UserGroup> response = new ApiResponse<>(200, "Group fetched successfully.", groupService.getGroupById(groupId));
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping("/join/{groupId}")
    public ResponseEntity<ApiResponse<GroupMember>> joinGroup(@PathVariable Long groupId) {
        ApiResponse<GroupMember> response = new ApiResponse<>(200, "Group joined successfully.", memberService.joinGroup(groupId));
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping("/leave/{groupId}")
    public ResponseEntity<ApiResponse<Object>> leaveGroup(@PathVariable Long groupId) {
        memberService.leaveGroup(groupId);
        ApiResponse<Object> response = new ApiResponse<>(200, "Group left successfully.", null);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/members/{groupId}")
    public ResponseEntity<ApiResponse<List<GroupMember>>> getGroupMembers(@PathVariable Long groupId) {
        ApiResponse<List<GroupMember>> response = new ApiResponse<>(200, "Group members fetched successfully.", memberService.getGroupMembers(groupId));
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping("/{groupId}/send")
    public ResponseEntity<ApiResponse<GroupPost>> createGroupPost(@PathVariable Long groupId, @RequestBody GroupPostRequest request) {
        ApiResponse<GroupPost> response = new ApiResponse<>(201, "Message posted in group.", groupPostService.createPost(groupId, request.getText()));
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/messages/{groupId}")
    public ResponseEntity<ApiResponse<List<GroupPost>>> getGroupMessages(@PathVariable Long groupId) {
        ApiResponse<List<GroupPost>> response = new ApiResponse<>(200, "Messages fetched successfully.", groupPostService.getGroupPosts(groupId));
        return ResponseEntity.status(200).body(response);
    }
}
