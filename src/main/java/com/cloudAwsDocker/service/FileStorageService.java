package com.cloudAwsDocker.service;

import com.cloudAwsDocker.dto.FileUploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageService {
    FileUploadResponse storeFile(MultipartFile file, String uploadedBy) throws IOException;
    byte[] loadFileAsBytes(String fileId) throws IOException;
    boolean deleteFile(String fileId);
    FileUploadResponse getFileInfo(String fileId);
}
