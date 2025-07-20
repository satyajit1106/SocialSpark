package com.hashedin.huSpark.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.Date;

@Getter
public class ApiResponse<T> {
    private final Integer status;
    private final String message;
    private final T payload;
    private final boolean success;
    private final String timestamp;

    public ApiResponse(Integer status, String message, T payload) {
        this.status = status;
        this.message = message;
        this.payload = payload;
        this.success = status < 400;
        this.timestamp = new Date().toString();
    }
}