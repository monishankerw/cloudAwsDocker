package com.cloudAwsDocker.controller;

import com.cloudAwsDocker.entity.User;
import com.cloudAwsDocker.repository.UserRepository;
import com.cloudAwsDocker.service.EmailService;
import com.cloudAwsDocker.service.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
public class TestController {

    private final EmailService emailService;
    private final SmsService smsService;
    private final UserRepository userRepository;

    @PostMapping("/send-email")
    public ResponseEntity<String> testEmail(@RequestParam String email) {
        try {
            emailService.sendOtpEmail(email, "123456");
            return ResponseEntity.ok("Test email sent successfully to " + email);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to send test email: " + e.getMessage());
        }
    }

    @PostMapping("/send-sms")
    public ResponseEntity<String> testSms(@RequestParam String phoneNumber) {
        try {
            smsService.sendOtpSms(phoneNumber, "123456");
            return ResponseEntity.ok("Test SMS sent successfully to " + phoneNumber);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to send test SMS: " + e.getMessage());
        }
    }

    @PostMapping("/send-otp")
    public ResponseEntity<String> testOtpFlow(
            @RequestParam String email,
            @RequestParam(required = false) String phoneNumber) {
        try {
            // Create a test user
            User user = new User();
            user.setEmail(email);
            if (phoneNumber != null) {
                user.setMobile(phoneNumber);
            }
            user.setUsername("testuser" + System.currentTimeMillis());
            user.setPassword("testpass123");
            user = userRepository.save(user);

            // Send OTPs
            if (phoneNumber != null) {
                smsService.sendOtpSms(phoneNumber, "123456");
            }
            emailService.sendOtpEmail(email, "123456");

            return ResponseEntity.ok("Test OTPs sent successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to send test OTPs: " + e.getMessage());
        }
    }
}
