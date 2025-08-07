package com.cloudAwsDocker.service.impl;

import com.cloudAwsDocker.dto.ExcelDataDto;
import com.cloudAwsDocker.dto.FileUploadResponse;
import com.cloudAwsDocker.entity.ExcelData;
import com.cloudAwsDocker.enums.ProcessingStatus;
import com.cloudAwsDocker.exception.ExcelProcessingException;
import com.cloudAwsDocker.repository.ExcelDataRepository;
import com.cloudAwsDocker.service.ExcelProcessingService;
import com.cloudAwsDocker.service.FileStorageService;
import org.modelmapper.ModelMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelProcessingServiceImpl implements ExcelProcessingService {

    private final ExcelDataRepository excelDataRepository;
    private final FileStorageService fileStorageService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public List<ExcelDataDto> processExcelFile(MultipartFile file, String uploadedBy) {
        List<ExcelData> excelDataList = new ArrayList<>();
        String fileName = file.getOriginalFilename();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                String sheetName = sheet.getSheetName();

                for (Row row : sheet) {
                    for (Cell cell : row) {
                        ExcelData excelData = new ExcelData();
                        excelData.setFileName(fileName);
                        excelData.setSheetName(sheetName);
                        excelData.setRowNumber(row.getRowNum() + 1); // 1-based index
                        excelData.setColumnName(getColumnName(cell.getColumnIndex()));
                        excelData.setCellValue(getCellValueAsString(cell));
                        excelData.setProcessedBy(uploadedBy);
                        excelData.setStatus(ProcessingStatus.PROCESSED);

                        excelDataList.add(excelData);
                    }
                }
            }

            // Save all records in batch
            excelDataRepository.saveAll(excelDataList);

            return excelDataList.stream()
                    .map(this::convertToDto)
                    .toList();

        } catch (IOException e) {
            log.error("Error processing Excel file: {}", e.getMessage(), e);
            throw new ExcelProcessingException("Error processing Excel file: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Map<String, Object> processAndSaveExcel(MultipartFile file, String uploadedBy) {
        // First save the file using FileStorageService
        FileUploadResponse fileResponse;
        try {
            fileResponse = fileStorageService.storeFile(file, uploadedBy);
        } catch (IOException e) {
            throw new ExcelProcessingException("Error storing Excel file: " + e.getMessage());
        }

        // Process the Excel file
        List<ExcelDataDto> processedData = processExcelFile(file, uploadedBy);

        // Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("fileId", fileResponse.getId());
        response.put("fileName", file.getOriginalFilename());
        response.put("recordsProcessed", processedData.size());
        response.put("status", "SUCCESS");
        response.put("timestamp", LocalDateTime.now());

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExcelDataDto> getExcelDataByFileName(String fileName) {
        return excelDataRepository.findByFileName(fileName, Pageable.unpaged()).stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExcelDataDto> getExcelDataByStatus(String status) {
        ProcessingStatus processingStatus = ProcessingStatus.valueOf(status.toUpperCase());
        return excelDataRepository.findByStatus(processingStatus).stream()
                .map(this::convertToDto)
                .toList();
    }

    @Async
    public CompletableFuture<List<ExcelDataDto>> processExcelFileAsync(MultipartFile file, String uploadedBy) {
        return CompletableFuture.completedFuture(processExcelFile(file, uploadedBy));
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC ->
                DateUtil.isCellDateFormatted(cell) ?
                    cell.getDateCellValue().toString() :
                    String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }

    private String getColumnName(int columnIndex) {
        StringBuilder columnName = new StringBuilder();
        int dividend = columnIndex + 1;
        int modulo;

        while (dividend > 0) {
            modulo = (dividend - 1) % 26;
            columnName.insert(0, (char) ('A' + modulo));
            dividend = (dividend - modulo) / 26;
        }

        return columnName.toString();
    }

    private ExcelDataDto convertToDto(ExcelData excelData) {
        return modelMapper.map(excelData, ExcelDataDto.class);
    }
}
