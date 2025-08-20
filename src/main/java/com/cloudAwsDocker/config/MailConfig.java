package com.cloudAwsDocker.config;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {
    private static final Logger log = LoggerFactory.getLogger(MailConfig.class);

    @Bean
    @ConditionalOnProperty(name = "spring.mail.host")
    public JavaMailSender javaMailSender(
            @Value("${spring.mail.host:}") String host,
            @Value("${spring.mail.port:587}") int port,
            @Value("${spring.mail.username:}") String username,
            @Value("${spring.mail.password:}") String password,
            @Value("${spring.mail.properties.mail.smtp.auth:true}") boolean auth,
            @Value("${spring.mail.properties.mail.smtp.starttls.enable:true}") boolean starttls) {
        
        if (host == null || host.isEmpty()) {
            log.warn("Email configuration is incomplete. Emails will not be sent.");
            return new NoOpMailSender();
        }

        log.info("Configuring mail sender for host: {}", host);
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        
        if (username != null && !username.isEmpty()) {
            mailSender.setUsername(username);
            mailSender.setPassword(password != null ? password : "");
        }
        
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", auth);
        props.put("mail.smtp.starttls.enable", starttls);
        props.put("mail.debug", "true");
        
        mailSender.setJavaMailProperties(props);
        return mailSender;
    }

    /**
     * A no-op implementation for when email is not configured
     */
    static class NoOpMailSender extends JavaMailSenderImpl {
        @Override
        protected void doSend(MimeMessage[] mimeMessages, Object[] originalMessages) {
            log.warn("Email sending is not configured. No emails will be sent.");
        }
    }
    
    /**
     * Check if the provided mail sender is a no-op implementation
     */
    public static boolean isNoOpMailSender(JavaMailSender mailSender) {
        return mailSender instanceof NoOpMailSender;
    }
}
