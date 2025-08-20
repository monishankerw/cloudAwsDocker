package com.cloudAwsDocker.service;

import com.cloudAwsDocker.config.MailConfig;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username:no-reply@example.com}")
    private String fromEmail;
    
    @Value("${app.name:Your App}")
    private String appName;
    
    private boolean enabled;
    
    @PostConstruct
    public void init() {
        this.enabled = !MailConfig.isNoOpMailSender(mailSender);
        
        if (!enabled) {
            log.warn("Email service is running in no-op mode. No emails will be sent.");
        } else {
            log.info("Email service initialized with sender: {}", this.fromEmail);
        }
    }

    public void sendOtpEmail(String to, String otp) {
        if (!enabled) {
            log.warn("Email sending is disabled. Would send OTP {} to {}", otp, to);
            return;
        }

        if (!StringUtils.hasText(to) || !StringUtils.hasText(otp)) {
            log.error("Cannot send email: recipient email or OTP is empty");
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            String subject = String.format("%s - Verify Your Email", appName);
            String htmlContent = String.format("""
                <html>
                    <body>
                        <h2>Email Verification</h2>
                        <p>Hello,</p>
                        <p>Thank you for registering with %s. Please use the following OTP to verify your email address:</p>
                        <h3 style="color: #2563eb; font-size: 24px; letter-spacing: 2px; padding: 10px 20px; background-color: #f3f4f6; display: inline-block; border-radius: 4px;">%s</h3>
                        <p>This OTP is valid for 15 minutes.</p>
                        <p>If you didn't request this, please ignore this email.</p>
                        <p>Best regards,<br>%s Team</p>
                    </body>
                </html>
                """, appName, otp, appName);

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = isHtml
            
            mailSender.send(message);
            log.info("OTP email sent to {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send OTP email to {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }
}
