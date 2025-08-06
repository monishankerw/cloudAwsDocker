package com.cloudAwsDocker.entity;

import com.cloudAwsDocker.enums.FileStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;


import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "file_metadata")
public class FileMetadata {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    
    @Column(nullable = false)
    private String fileName;
    
    @Column(nullable = false)
    private String fileType;
    
    @Column(nullable = false)
    private long size;
    
    @Column(nullable = false, unique = true)
    private String storagePath;
    
    @Column(nullable = false)
    private String originalFileName;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime uploadDate;
    
    @UpdateTimestamp
    private LocalDateTime lastModifiedDate;
    
    @Column
    private String uploadedBy;
    
    @Enumerated(EnumType.STRING)
    private FileStatus status = FileStatus.ACTIVE;
}
