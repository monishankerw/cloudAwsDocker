package com.cloudAwsDocker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {
    private String id;
    private String fileName;
    private String fileType;
    private long size;
    private String downloadUrl;
    private LocalDateTime uploadDate;
    private String uploadStatus;
}
