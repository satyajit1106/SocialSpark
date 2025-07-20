package com.hashedin.huSpark.api;

import com.hashedin.huSpark.dto.response.ApiResponse;
import com.hashedin.huSpark.entity.BirthdayWish;
import com.hashedin.huSpark.service.WishService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/wishes")
@AllArgsConstructor
public class WishController {
    private final WishService wishService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<BirthdayWish>>> getAllWishes() {
        ApiResponse<List<BirthdayWish>> response = new ApiResponse<>(200, "Wishes fetched successfully.", wishService.getAll());
        return ResponseEntity.status(200).body(response);
    }
}
