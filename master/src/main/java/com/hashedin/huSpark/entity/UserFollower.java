package com.hashedin.huSpark.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_followers", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "follower_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFollower {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    @Column(nullable = false)
    private LocalDateTime followedAt;

//    @PrePersist
//    protected void onCreate() {
//        this.followedAt = LocalDateTime.now();
//    }
}
