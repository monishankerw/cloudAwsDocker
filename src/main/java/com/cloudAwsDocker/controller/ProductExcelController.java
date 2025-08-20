package com.cloudAwsDocker.controller;

import com.cloudAwsDocker.dto.ApiResponse;
import com.cloudAwsDocker.dto.ExcelImportResponse;
import com.cloudAwsDocker.service.ProductExcelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/products/excel")
@Slf4j
@RequiredArgsConstructor
public class ProductExcelController {
    private final ProductExcelService productExcelService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<ExcelImportResponse>> uploadFile(@RequestParam("file") MultipartFile file) {
        log.info("Received file upload request: {}", file.getOriginalFilename());

        if (file.isEmpty()) {
            log.warn("Empty file received in upload request");
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Please select a file to upload", HttpStatus.BAD_REQUEST.value()));
        }

        try {
            ExcelImportResponse response = productExcelService.importProducts(file);
            log.info("File processed successfully: {}", file.getOriginalFilename());

            return ResponseEntity.ok(ApiResponse.success(response, "File processed successfully"));
        } catch (Exception e) {
            log.error("Error processing file: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to process file: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile() {
        String filename = "products_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) + ".xlsx";
        log.info("Download request for products Excel file: {}", filename);

        ByteArrayInputStream in = productExcelService.exportProducts();
        byte[] bytes = in.readAllBytes();
        ByteArrayResource resource = new ByteArrayResource(bytes);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(bytes.length)
                .body(resource);
    }

    @GetMapping("/template")
    public ResponseEntity<Resource> downloadTemplate() {
        log.info("Download request for Excel template");
        String filename = "products_template.xlsx";

        ByteArrayInputStream in = productExcelService.downloadTemplate();
        byte[] bytes = in.readAllBytes();
        ByteArrayResource resource = new ByteArrayResource(bytes);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(bytes.length)
                .body(resource);
    }

    @GetMapping("/file/{id}")
    public ResponseEntity<Resource> downloadUploadedFile(@PathVariable Long id) {
        // This would require additional service method to retrieve the file from storage
        // Implementation depends on how you want to handle file retrieval
        return ResponseEntity.notFound().build();
    }
}