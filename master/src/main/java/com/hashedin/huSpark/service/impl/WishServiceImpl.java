package com.hashedin.huSpark.service.impl;

import com.hashedin.huSpark.entity.BirthdayWish;
import com.hashedin.huSpark.repository.BirthdayWishRepository;
import com.hashedin.huSpark.service.WishService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class WishServiceImpl implements WishService {
    private final BirthdayWishRepository wishRepository;

    /**
     * @return
     */
    @Override
    public List<BirthdayWish> getAll() {
        return wishRepository.findAll();
    }
}
