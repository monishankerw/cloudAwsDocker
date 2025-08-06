package com.cloudAwsDocker.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class FileUtils {
    
    private FileUtils() {
        // Private constructor to prevent instantiation
    }
    
    public static String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }
    
    public static String generateUniqueFileName(String originalFileName) {
        String fileExtension = getFileExtension(originalFileName);
        return String.format("%s.%s", java.util.UUID.randomUUID(), fileExtension);
    }
    
    public static String getContentType(Path filePath) throws IOException {
        return Files.probeContentType(filePath);
    }
    
    public static boolean isImageFile(MultipartFile file) {
        String fileExtension = getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
        return fileExtension.matches("(?i)(jpg|jpeg|png|gif|bmp|webp)");
    }
    
    public static boolean isDocumentFile(MultipartFile file) {
        String fileExtension = getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
        return fileExtension.matches("(?i)(pdf|doc|docx|txt|rtf|odt)");
    }
    
    public static boolean isArchiveFile(MultipartFile file) {
        String fileExtension = getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
        return fileExtension.matches("(?i)(zip|rar|7z|tar|gz)");
    }
}
