package com.cloudAwsDocker.controller;

import com.cloudAwsDocker.dto.ExcelDataDto;
import com.cloudAwsDocker.service.ExcelProcessingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/excel")
@RequiredArgsConstructor
@Tag(name = "Excel Processing", description = "APIs for Excel file processing")
public class ExcelController {

    private final ExcelProcessingService excelProcessingService;

    @PostMapping("/upload")
    @Operation(summary = "Upload and process an Excel file")
    public ResponseEntity<Map<String, Object>> uploadExcel(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {

        String uploadedBy = userDetails != null ? userDetails.getUsername() : "system";
        Map<String, Object> response = excelProcessingService.processAndSaveExcel(file, uploadedBy);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/file/{fileName}")
    @Operation(summary = "Get processed Excel data by file name")
    public ResponseEntity<List<ExcelDataDto>> getExcelDataByFileName(
            @PathVariable String fileName) {

        List<ExcelDataDto> data = excelProcessingService.getExcelDataByFileName(fileName);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get Excel data by processing status")
    public ResponseEntity<List<ExcelDataDto>> getExcelDataByStatus(
            @PathVariable String status) {

        List<ExcelDataDto> data = excelProcessingService.getExcelDataByStatus(status);
        return ResponseEntity.ok(data);
    }

    @PostMapping("/process")
    @Operation(summary = "Process Excel file without saving to database")
    public ResponseEntity<List<ExcelDataDto>> processExcel(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {

        String uploadedBy = userDetails != null ? userDetails.getUsername() : "system";
        List<ExcelDataDto> result = excelProcessingService.processExcelFile(file, uploadedBy);
        return ResponseEntity.ok(result);
    }
}
