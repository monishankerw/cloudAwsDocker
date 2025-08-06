package com.cloudAwsDocker.controller;

import com.cloudAwsDocker.dto.FileUploadResponse;
import com.cloudAwsDocker.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        
        String uploadedBy = userDetails != null ? userDetails.getUsername() : "anonymous";
        FileUploadResponse response = fileStorageService.storeFile(file, uploadedBy);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileId) throws IOException {
        byte[] fileContent = fileStorageService.loadFileAsBytes(fileId);
        FileUploadResponse fileInfo = fileStorageService.getFileInfo(fileId);
        
        ByteArrayResource resource = new ByteArrayResource(fileContent);
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileInfo.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"" + fileInfo.getFileName() + "\"")
                .body(resource);
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<FileUploadResponse> getFileInfo(@PathVariable String fileId) {
        FileUploadResponse fileInfo = fileStorageService.getFileInfo(fileId);
        return ResponseEntity.ok(fileInfo);
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> deleteFile(@PathVariable String fileId) {
        boolean deleted = fileStorageService.deleteFile(fileId);
        return deleted ? 
                ResponseEntity.noContent().build() : 
                ResponseEntity.notFound().build();
    }
}
