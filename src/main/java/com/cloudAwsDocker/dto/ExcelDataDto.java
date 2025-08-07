package com.cloudAwsDocker.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ExcelDataDto {
    private String id;
    private String fileName;
    private String sheetName;
    private int rowNumber;
    private String columnName;
    private String cellValue;
    private LocalDateTime processedAt;
    private String processedBy;
    private String status; // PENDING, PROCESSED, ERROR
    private String errorMessage;
}
