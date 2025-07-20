package com.hashedin.huSpark.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class RegisterUserRequest {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters.")
    private String name;

    @NotBlank
    @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters.")
    private String password;

    @Past(message = "Date of birth must be in the past.")
    private LocalDate dob;
}
