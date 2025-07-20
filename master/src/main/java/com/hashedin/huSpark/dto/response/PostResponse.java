package com.hashedin.huSpark.dto.response;

import com.hashedin.huSpark.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostResponse {
    private Long id;
    private User user;
    private String text;
    private String filePath;

    public PostResponse(Long id, User user, String text, String filePath) {
        this.id = id;
        this.user = user;
        this.text = text;
        this.filePath = filePath;
    }
}
