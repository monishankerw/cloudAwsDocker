package com.cloudAwsDocker.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OtpVerificationRequest {
    @NotBlank(message = "Email or mobile is required")
    private String emailOrMobile;
    
    @NotBlank(message = "OTP is required")
    private String otp;
}
