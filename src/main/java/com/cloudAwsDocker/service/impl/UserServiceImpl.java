package com.cloudAwsDocker.service.impl;

import com.cloudAwsDocker.dto.OtpVerificationRequest;
import com.cloudAwsDocker.dto.UserRequest;
import com.cloudAwsDocker.dto.UserResponse;
import com.cloudAwsDocker.entity.User;

import com.cloudAwsDocker.enums.UserRole;
import com.cloudAwsDocker.exception.OtpException;
import com.cloudAwsDocker.exception.ResourceAlreadyExistsException;
import com.cloudAwsDocker.exception.ResourceNotFoundException;
import com.cloudAwsDocker.repository.UserRepository;
import com.cloudAwsDocker.service.OtpService;
import com.cloudAwsDocker.service.UserService;
import com.cloudAwsDocker.util.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponse registerUser(UserRequest userRequest) {
        // Check if username already exists
        if (userRepository.existsByUsername(userRequest.getUsername())) {
            throw new ResourceAlreadyExistsException("Username is already taken!");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new ResourceAlreadyExistsException("Email is already in use!");
        }

        // Check if mobile already exists
        if (userRepository.existsByMobile(userRequest.getMobile())) {
            throw new ResourceAlreadyExistsException("Mobile number is already in use!");
        }

        // Create new user
        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setEmail(userRequest.getEmail());
        user.setMobile(userRequest.getMobile());
        user.setRole(UserRole.ROLE_USER); // Default role for new users
        user.setActive(false); // User needs to verify email and mobile first

        // Save user first to get an ID
        User savedUser = userRepository.save(user);

        // Generate and send OTP for email verification
        otpService.generateAndSendOtp(savedUser, true);

        return userMapper.mapToUserResponse(savedUser);
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return userMapper.mapToUserResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::mapToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest userRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        if (userRequest.getUsername() != null) {
            user.setUsername(userRequest.getUsername());
        }
        if (userRequest.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        }
        if (userRequest.getEmail() != null) {
            user.setEmail(userRequest.getEmail());
        }

        User updatedUser = userRepository.save(user);
        return userMapper.mapToUserResponse(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        userRepository.delete(user);
    }

    @Override
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return userMapper.mapToUserResponse(user);
    }
    
    @Override
    @Transactional
    public String initiateEmailVerification(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
                
        if (user.isEmailVerified()) {
            throw new IllegalStateException("Email is already verified");
        }
        
        return otpService.generateAndSendOtp(user, true);
    }
    
    @Override
    @Transactional
    public String initiateMobileVerification(String mobile) {
        User user = userRepository.findByEmailOrMobile(mobile)
                .orElseThrow(() -> new ResourceNotFoundException("User", "mobile", mobile));
                
        if (user.isMobileVerified()) {
            throw new IllegalStateException("Mobile number is already verified");
        }
        
        return otpService.generateAndSendOtp(user, false);
    }
    
    @Override
    @Transactional
    public boolean verifyEmailOtp(OtpVerificationRequest request) {
        User user = userRepository.findByEmail(request.getEmailOrMobile())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmailOrMobile()));
        
        if (user.isEmailVerified()) {
            throw new IllegalStateException("Email is already verified");
        }
        
        boolean verified = otpService.verifyOtp(request.getEmailOrMobile(), request.getOtp(), true);
        
        if (verified) {
            user.setEmailVerified(true);
            // If mobile is also verified, activate the account
            if (user.isMobileVerified()) {
                user.setActive(true);
            }
            userRepository.save(user);
        }
        
        return verified;
    }
    
    @Override
    @Transactional
    public boolean verifyMobileOtp(OtpVerificationRequest request) {
        User user = userRepository.findByEmailOrMobile(request.getEmailOrMobile())
                .orElseThrow(() -> new ResourceNotFoundException("User", "mobile", request.getEmailOrMobile()));
        
        if (user.isMobileVerified()) {
            throw new IllegalStateException("Mobile number is already verified");
        }
        
        boolean verified = otpService.verifyOtp(request.getEmailOrMobile(), request.getOtp(), false);
        
        if (verified) {
            user.setMobileVerified(true);
            // If email is also verified, activate the account
            if (user.isEmailVerified()) {
                user.setActive(true);
            }
            userRepository.save(user);
        }
        
        return verified;
    }
    
    @Override
    @Transactional
    public String resendEmailVerification(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
                
        if (user.isEmailVerified()) {
            throw new IllegalStateException("Email is already verified");
        }
        
        return otpService.resendOtp(email, true);
    }
    
    @Override
    @Transactional
    public String resendMobileVerification(String mobile) {
        User user = userRepository.findByEmailOrMobile(mobile)
                .orElseThrow(() -> new ResourceNotFoundException("User", "mobile", mobile));
                
        if (user.isMobileVerified()) {
            throw new IllegalStateException("Mobile number is already verified");
        }
        
        return otpService.resendOtp(mobile, false);
    }
}
