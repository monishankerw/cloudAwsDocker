package com.cloudAwsDocker.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
class ErrorResponse {
    private String errorCode;
    private String message;
    private LocalDateTime timestamp;
}