package com.hashedin.huSpark.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @NotBlank
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters.")
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters.")
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Past(message = "Date of birth must be in the past.")
    @Column(nullable = false)
    private LocalDate dob;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Visibility visibility;

    @Column(nullable = false)
    private boolean isActive;

    private LocalDateTime lastPasswordUpdated;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

//    @PrePersist
//    protected void onCreate() {
//        createdAt = LocalDateTime.now();
//        updatedAt = LocalDateTime.now();
//        lastPasswordUpdated = LocalDateTime.now();
//        visibility = Visibility.PUBLIC;
//        isActive = true;
//    }

//    @PreUpdate
//    protected void onUpdate() {
//        updatedAt = LocalDateTime.now();
//    }

    public boolean isPasswordValid() {
        return lastPasswordUpdated.isAfter(LocalDateTime.now().minusDays(30));
    }
}
