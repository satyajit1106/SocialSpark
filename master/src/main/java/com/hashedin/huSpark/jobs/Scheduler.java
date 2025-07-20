package com.hashedin.huSpark.jobs;

import com.hashedin.huSpark.entity.*;
import com.hashedin.huSpark.repository.BirthdayWishRepository;
import com.hashedin.huSpark.repository.PostRepository;
import com.hashedin.huSpark.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@Slf4j
public class Scheduler {
    private final UserRepository userRepository;
    private final BirthdayWishRepository wishRepository;

    @Scheduled(cron = "0 0 0 * * *")
    public void checkBirthdays() {
        log.info("Checking for birthdays at: {}", LocalDateTime.now());
        List<User> user = userRepository.findByDob(LocalDate.now());

        List<User> usersToWish = user.stream().filter(item -> {
            return item.getDob().isEqual(LocalDate.now());
        }).toList();

        usersToWish.forEach(u -> {
            BirthdayWish birthdayWish = BirthdayWish
                    .builder()
                    .wishText("Happy birthday " + u.getName().toUpperCase() + "!")
                    .expiresAt(LocalDateTime.now().plusDays(1))
                    .build();

            wishRepository.save(birthdayWish);

            log.info("Wished user account: {}", u.getEmail());
        });

        System.out.println("Wished users!");
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void deleteExpiredEntries() {
        List<BirthdayWish> expiredWishes = wishRepository.findByExpiresAt(LocalDate.now());

        expiredWishes.forEach(wishRepository::delete);
    }
}
