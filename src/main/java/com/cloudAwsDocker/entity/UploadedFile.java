package com.cloudAwsDocker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "uploaded_files")
public class UploadedFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String filename;
    private String originalFilename;
    private String filePath;
    private Long fileSize;
    private String contentType;
    
    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;
    
    private Integer totalRecords;
    private Integer processedRecords;
    private Integer failedRecords;
    
    @Enumerated(EnumType.STRING)
    private FileStatus status;
    
    // Pre-persist hook
    @PrePersist
    public void prePersist() {
        this.uploadedAt = LocalDateTime.now();
    }
    
    public enum FileStatus {
        UPLOADED, PROCESSING, COMPLETED, FAILED
    }
}