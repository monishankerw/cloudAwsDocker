package com.cloudAwsDocker.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private Integer statusCode;
    private LocalDateTime timestamp;
    private String path;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(
                true,
                "Operation completed successfully",
                data,
                HttpStatus.OK.value(),
                LocalDateTime.now(),
                null
        );
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(
                true,
                message,
                data,
                HttpStatus.OK.value(),
                LocalDateTime.now(),
                null
        );
    }

    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(
                true,
                "Resource created successfully",
                data,
                HttpStatus.CREATED.value(),
                LocalDateTime.now(),
                null
        );
    }

    public static <T> ApiResponse<T> error(String message, int statusCode) {
        return new ApiResponse<>(
                false,
                message,
                null,
                statusCode,
                LocalDateTime.now(),
                null
        );
    }

    public static <T> ApiResponse<T> error(String message, int statusCode, String path) {
        return new ApiResponse<>(
                false,
                message,
                null,
                statusCode,
                LocalDateTime.now(),
                path
        );
    }

    public ApiResponse(boolean success, String message, T data, Integer statusCode, LocalDateTime timestamp, String path) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.statusCode = statusCode;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
        this.path = path;
    }
}
