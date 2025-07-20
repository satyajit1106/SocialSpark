package com.hashedin.huSpark.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BirthdayWish {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String wishText;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

//    @PrePersist
//    protected void onCreate() {
//        this.expiresAt = LocalDateTime.now().plusDays(1);
//    }
}
