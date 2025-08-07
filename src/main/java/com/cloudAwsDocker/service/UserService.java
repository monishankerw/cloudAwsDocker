package com.cloudAwsDocker.service;

import com.cloudAwsDocker.dto.OtpVerificationRequest;
import com.cloudAwsDocker.dto.UserRequest;
import com.cloudAwsDocker.dto.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse registerUser(UserRequest userRequest);
    UserResponse getUserById(Long id);
    List<UserResponse> getAllUsers();
    UserResponse updateUser(Long id, UserRequest userRequest);
    void deleteUser(Long id);
    UserResponse getUserByUsername(String username);
    
    // OTP Verification related methods
    String initiateEmailVerification(String email);
    String initiateMobileVerification(String mobile);
    boolean verifyEmailOtp(OtpVerificationRequest request);
    boolean verifyMobileOtp(OtpVerificationRequest request);
    String resendEmailVerification(String email);
    String resendMobileVerification(String mobile);
}
