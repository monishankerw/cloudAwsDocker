package com.cloudAwsDocker.service;

import com.cloudAwsDocker.entity.User;
import com.cloudAwsDocker.exception.OtpException;
import com.cloudAwsDocker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OtpService {
    private static final int OTP_LENGTH = 6;
    private static final long OTP_EXIRATION_MINUTES = 15;
    
    @Value("${app.otp.test-mode:false}")
    private boolean testMode;
    
    private final UserRepository userRepository;
    
    public OtpService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Transactional
    public String generateAndSendOtp(User user, boolean isEmailVerification) {
        String otp = generateOtp();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(OTP_EXIRATION_MINUTES);
        
        if (isEmailVerification) {
            user.setEmailVerificationOtp(otp);
        } else {
            user.setMobileVerificationOtp(otp);
        }
        user.setOtpExpiryTime(expiryTime);
        userRepository.save(user);
        
        // In a real application, you would send the OTP via email/SMS here
        // For now, we'll just log it or return it directly in test mode
        if (testMode) {
            return otp;
        }
        
        // TODO: Implement actual email/SMS sending
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
        // Generate a 6-digit OTP
        SecureRandom random = new SecureRandom();
        int num = 100000 + random.nextInt(900000);
        return String.valueOf(num);
    }
}
