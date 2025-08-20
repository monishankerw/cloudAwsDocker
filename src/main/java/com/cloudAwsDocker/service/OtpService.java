package com.cloudAwsDocker.service;

import com.cloudAwsDocker.entity.User;
import com.cloudAwsDocker.exception.OtpException;
import com.cloudAwsDocker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)

@Service
public class OtpService {
    private static final int OTP_LENGTH = 6;
    private static final long OTP_EXIRATION_MINUTES = 15;
    
        @Value("${app.otp.test-mode:false}")
    private boolean testMode;
    
    @Value("${app.otp.length:6}")
    private int otpLength;
    
    @Value("${app.otp.expiration-minutes:15}")
    private int otpExpirationMinutes;
    
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final SmsService smsService;
    private final SecureRandom random = new SecureRandom();
    
    @Transactional
    public String generateAndSendOtp(User user, boolean isEmailVerification) {
        String otp = generateOtp();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(otpExpirationMinutes);
        
        if (isEmailVerification) {
            user.setEmailVerificationOtp(otp);
            user.setOtpExpiryTime(expiryTime);
            userRepository.save(user);
            
            if (testMode) {
                log.info("Test mode: Email OTP for {} is {}", user.getEmail(), otp);
                return otp;
            }
            
            try {
                emailService.sendOtpEmail(user.getEmail(), otp);
                log.info("Email OTP sent to {}", user.getEmail());
            } catch (Exception e) {
                log.error("Failed to send email OTP to {}: {}", user.getEmail(), e.getMessage(), e);
                throw new OtpException("Failed to send OTP. Please try again later.");
            }
        } else {
            user.setMobileVerificationOtp(otp);
            user.setOtpExpiryTime(expiryTime);
            userRepository.save(user);
            
            if (testMode) {
                log.info("Test mode: Mobile OTP for {} is {}", user.getMobile(), otp);
                return otp;
            }
            
            try {
                smsService.sendOtpSms(user.getMobile(), otp);
                log.info("SMS OTP sent to {}", user.getMobile());
            } catch (Exception e) {
                log.error("Failed to send SMS OTP to {}: {}", user.getMobile(), e.getMessage(), e);
                throw new OtpException("Failed to send OTP. Please try again later.");
            }
        }
        
        return null;
    }
    
    @Transactional
    public boolean verifyOtp(String emailOrMobile, String otp, boolean isEmailVerification) {
        Optional<User> userOpt = userRepository.findByEmailOrMobile(emailOrMobile);
        if (userOpt.isEmpty()) {
            throw new OtpException("User not found");
        }
        
        User user = userOpt.get();
        String storedOtp = isEmailVerification ? 
            user.getEmailVerificationOtp() : user.getMobileVerificationOtp();
            
        if (storedOtp == null || !storedOtp.equals(otp)) {
            throw new OtpException("Invalid OTP");
        }
        
        if (user.getOtpExpiryTime().isBefore(LocalDateTime.now())) {
            throw new OtpException("OTP has expired");
        }
        
        // Mark the appropriate verification as complete
        if (isEmailVerification) {
            user.setEmailVerified(true);
            user.setEmailVerificationOtp(null);
        } else {
            user.setMobileVerified(true);
            user.setMobileVerificationOtp(null);
        }
        
        // If both email and mobile are verified, activate the account
        if (user.isEmailVerified() && user.isMobileVerified()) {
            user.setActive(true);
        }
        
        user.setOtpExpiryTime(null);
        userRepository.save(user);
        return true;
    }
    
    @Transactional
    public String resendOtp(String emailOrMobile, boolean isEmailVerification) {
        Optional<User> userOpt = userRepository.findByEmailOrMobile(emailOrMobile);
        if (userOpt.isEmpty()) {
            throw new OtpException("User not found");
        }
        
        User user = userOpt.get();
        return generateAndSendOtp(user, isEmailVerification);
    }
    
    private String generateOtp() {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < otpLength; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }
}
