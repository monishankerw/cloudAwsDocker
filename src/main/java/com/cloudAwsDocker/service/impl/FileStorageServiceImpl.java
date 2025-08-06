package com.cloudAwsDocker.service.impl;

import com.cloudAwsDocker.dto.FileUploadResponse;
import com.cloudAwsDocker.entity.FileMetadata;
import com.cloudAwsDocker.enums.FileStatus;
import com.cloudAwsDocker.repository.FileMetadataRepository;
import com.cloudAwsDocker.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Transactional
public class FileStorageServiceImpl implements FileStorageService {

    private final Path fileStorageLocation;
    private final FileMetadataRepository fileMetadataRepository;

    public FileStorageServiceImpl(
            @Value("${file.upload-dir}") String uploadDir,
            FileMetadataRepository fileMetadataRepository) throws IOException {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.fileMetadataRepository = fileMetadataRepository;
        Files.createDirectories(this.fileStorageLocation);
    }

    @Override
    public FileUploadResponse storeFile(MultipartFile file, String uploadedBy) throws IOException {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        
        // Validate file
        if (originalFileName.contains("..")) {
            throw new RuntimeException("Invalid file name: " + originalFileName);
        }

        // Generate unique file name
        String fileExtension = "";
        if (originalFileName.lastIndexOf(".") > 0) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        String storageFileName = UUID.randomUUID().toString() + fileExtension;
        
        // Save file to disk
        Path targetLocation = this.fileStorageLocation.resolve(storageFileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        
        // Save file metadata to database
        FileMetadata metadata = new FileMetadata();
        metadata.setFileName(originalFileName);
        metadata.setFileType(file.getContentType());
        metadata.setSize(file.getSize());
        metadata.setStoragePath(storageFileName);
        metadata.setOriginalFileName(originalFileName);
        metadata.setUploadedBy(uploadedBy);
        metadata = fileMetadataRepository.save(metadata);
        
        // Prepare response
        return new FileUploadResponse(
            metadata.getId(),
            metadata.getFileName(),
            metadata.getFileType(),
            metadata.getSize(),
            "/api/v1/files/download/" + metadata.getId(),
            metadata.getUploadDate(),
            "UPLOADED"
        );
    }

    @Override
    public byte[] loadFileAsBytes(String fileId) throws IOException {
        FileMetadata metadata = fileMetadataRepository.findByIdAndStatus(fileId, FileStatus.ACTIVE)
            .orElseThrow(() -> new RuntimeException("File not found with id: " + fileId));

        Path filePath = this.fileStorageLocation.resolve(metadata.getStoragePath()).normalize();
        return Files.readAllBytes(filePath);
    }

    @Override
    public boolean deleteFile(String fileId) {
        return fileMetadataRepository.findById(fileId)
            .map(file -> {
                file.setStatus(FileStatus.DELETED);
                fileMetadataRepository.save(file);
                // Note: We're not deleting the actual file, just marking it as deleted in the database
                return true;
            })
            .orElse(false);
    }

    @Override
    public FileUploadResponse getFileInfo(String fileId) {
        return fileMetadataRepository.findById(fileId)
            .map(file -> new FileUploadResponse(
                file.getId(),
                file.getFileName(),
                file.getFileType(),
                file.getSize(),
                "/api/v1/files/download/" + file.getId(),
                file.getUploadDate(),
                file.getStatus().name()
            ))
            .orElseThrow(() -> new RuntimeException("File not found with id: " + fileId));
    }
}
