package com.cloudAwsDocker.service;

import com.cloudAwsDocker.dto.ExcelDataDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ExcelProcessingService {

    /**
     * Process an Excel file and return the extracted data
     * @param file The Excel file to process
     * @param uploadedBy User who uploaded the file
     * @return List of processed Excel data DTOs
     */
    List<ExcelDataDto> processExcelFile(MultipartFile file, String uploadedBy);

    /**
//     * Process an Excel file and save the data to the database
     * @param file The Excel file to process
     * @param uploadedBy User who uploaded the file
     * @return Summary of the processing operation
     */
    Map<String, Object> processAndSaveExcel(MultipartFile file, String uploadedBy);

    /**
     * Get all Excel data for a specific file
     * @param fileName Name of the Excel file
     * @return List of Excel data DTOs
     */
    List<ExcelDataDto> getExcelDataByFileName(String fileName);

    /**
     * Get Excel data by status
     * @param status Processing status (PENDING, PROCESSED, ERROR)
     * @return List of Excel data DTOs matching the status
     */
    List<ExcelDataDto> getExcelDataByStatus(String status);
}
