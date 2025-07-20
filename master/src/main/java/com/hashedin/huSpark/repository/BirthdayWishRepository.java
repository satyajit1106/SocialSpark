package com.hashedin.huSpark.repository;

import com.hashedin.huSpark.entity.BirthdayWish;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BirthdayWishRepository extends JpaRepository<BirthdayWish, Long> {
    List<BirthdayWish> findByExpiresAt(LocalDate date);
}
