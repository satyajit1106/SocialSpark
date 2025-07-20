package com.hashedin.huSpark.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class PasswordChangeRequest {
    private String oldPassword;
    private String newPassword;
}
