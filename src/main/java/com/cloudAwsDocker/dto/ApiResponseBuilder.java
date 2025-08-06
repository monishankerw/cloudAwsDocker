package com.cloudAwsDocker.dto;

import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.WebRequest;

public class ApiResponseBuilder {
    private ApiResponseBuilder() {
        // Private constructor to prevent instantiation
    }

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.success(data);
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.success(data, message);
    }

    public static <T> ApiResponse<T> created(T data) {
        return ApiResponse.created(data);
    }

    public static <T> ApiResponse<T> error(String message, HttpStatus status) {
        return ApiResponse.error(message, status.value());
    }

    public static <T> ApiResponse<T> error(String message, HttpStatus status, WebRequest request) {
        return ApiResponse.error(message, status.value(), request.getDescription(false));
    }

    public static <T> ApiResponse<T> notFound(String resource, String field, Object value) {
        String message = String.format("%s not found with %s: %s", resource, field, value);
        return ApiResponse.error(message, HttpStatus.NOT_FOUND.value());
    }

    public static <T> ApiResponse<T> badRequest(String message) {
        return ApiResponse.error(message, HttpStatus.BAD_REQUEST.value());
    }

    public static <T> ApiResponse<T> unauthorized(String message) {
        return ApiResponse.error(message, HttpStatus.UNAUTHORIZED.value());
    }
}
