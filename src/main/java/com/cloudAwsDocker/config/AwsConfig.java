package com.cloudAwsDocker.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

@Configuration
public class AwsConfig {

    private static final Logger log = LoggerFactory.getLogger(AwsConfig.class);

    @Value("${aws.accessKeyId:}")
    private String awsAccessKey;

    @Value("${aws.secretKey:}")
    private String awsSecretKey;

    @Value("${aws.region:ap-south-1}")
    private String awsRegion;

    @Bean
    @ConditionalOnProperty(name = "aws.accessKeyId")
    public SnsClient snsClient() {
        if (!StringUtils.hasText(awsAccessKey) || !StringUtils.hasText(awsSecretKey)) {
            log.warn("AWS credentials are not properly configured. Creating a mock SnsClient.");
            return createMockSnsClient();
        }
        
        try {
            return SnsClient.builder()
                    .region(Region.of(awsRegion))
                    .credentialsProvider(
                        StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(awsAccessKey, awsSecretKey)
                        )
                    )
                    .build();
        } catch (Exception e) {
            log.warn("Failed to create SnsClient with provided credentials. Using mock client. Error: {}", e.getMessage());
            return createMockSnsClient();
        }
    }
    
    /**
     * Creates a mock SnsClient for development/testing when AWS credentials are not available
     */
    @Bean
    @ConditionalOnMissingBean(SnsClient.class)
    public SnsClient mockSnsClient() {
        log.warn("Using mock SnsClient. No real SMS will be sent.");
        return createMockSnsClient();
    }
    
    private SnsClient createMockSnsClient() {
        return new SnsClient() {
            @Override
            public String serviceName() {
                return "MockSnsClient";
            }
            
            @Override
            public void close() {
                // No-op
            }
            
            @Override
            public PublishResponse publish(PublishRequest publishRequest) {
                log.info("Mock SNS Publish - To: {}, Message: {}", 
                    publishRequest.phoneNumber(), 
                    publishRequest.message().substring(0, Math.min(50, publishRequest.message().length())) + "...");
                return PublishResponse.builder()
                    .messageId("mock-message-id-" + System.currentTimeMillis())
                    .build();
            }
            
            // Implement other required methods with no-op or mock implementations
            // ...
        };
    }
}
