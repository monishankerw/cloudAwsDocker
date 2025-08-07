package com.cloudAwsDocker.entity;

import com.cloudAwsDocker.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false, unique = true)
    private String mobile;
    
    @Enumerated(EnumType.STRING)
    private UserRole role;
    
    private boolean active = false; // Set to false until email is verified
    
    private boolean emailVerified = false;
    
    private boolean mobileVerified = false;
    
    private String emailVerificationOtp;
    
    private String mobileVerificationOtp;
    
    private LocalDateTime otpExpiryTime;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
