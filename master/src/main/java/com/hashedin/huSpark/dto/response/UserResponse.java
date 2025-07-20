package com.hashedin.huSpark.dto.response;

import com.hashedin.huSpark.entity.Role;
import com.hashedin.huSpark.entity.Visibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private Visibility visibility;
    private Role role;
    private LocalDate dob;
}
