package com.cloudAwsDocker.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExcelImportResponse {
    private int totalRecords;
    private int successfulImports;
    private int failedImports;
    private List<String> errorMessages;
    private String downloadUrl;
    private Long fileId;
    private String filePath;

    public ExcelImportResponse(int totalRecords) {
        this.totalRecords = totalRecords;
        this.successfulImports = 0;
        this.failedImports = 0;
        this.errorMessages = new java.util.ArrayList<>();
    }

    public void addErrorMessage(String message) {
        if (this.errorMessages == null) {
            this.errorMessages = new java.util.ArrayList<>();
        }
        this.errorMessages.add(message);
    }

    public void incrementSuccessfulImports() {
        this.successfulImports++;
    }

    public void incrementFailedImports() {
        this.failedImports++;
    }
}