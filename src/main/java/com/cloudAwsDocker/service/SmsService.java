package com.cloudAwsDocker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;

@Service
public class SmsService {
    private static final Logger log = LoggerFactory.getLogger(SmsService.class);
    
    private final SnsClient snsClient;
    
    @Value("${app.sms.senderId:YOURAPP}")
    private String senderId;
    
    @Value("${app.name:Your App}")
    private String appName;

    public SmsService(SnsClient snsClient) {
        this.snsClient = snsClient;
    }

    public void sendOtpSms(String phoneNumber, String otp) {
        if (!phoneNumber.startsWith("+")) {
            phoneNumber = "+91" + phoneNumber; // Default to India if no country code
        }
        
        String message = String.format("""
            %s: Your verification code is %s. Valid for 15 minutes. Do not share this code with anyone.
            """, appName, otp);
            
        try {
            PublishRequest request = PublishRequest.builder()
                .message(message)
                .phoneNumber(phoneNumber)
                .messageAttributes(
                    java.util.Map.of(
                        "AWS.SNS.SMS.SenderID", 
                        software.amazon.awssdk.services.sns.model.MessageAttributeValue.builder()
                            .dataType("String")
                            .stringValue(senderId)
                            .build(),
                        "AWS.SNS.SMS.SMSType", 
                        software.amazon.awssdk.services.sns.model.MessageAttributeValue.builder()
                            .dataType("String")
                            .stringValue("Transactional")
                            .build()
                    )
                )
                .build();
                
            PublishResponse result = snsClient.publish(request);
            log.info("SMS sent to {} with message ID: {}", phoneNumber, result.messageId());
        } catch (SnsException e) {
            log.error("Error sending SMS to {}: {}", phoneNumber, e.getMessage(), e);
            throw new RuntimeException("Failed to send SMS: " + e.getMessage(), e);
        }
    }
}