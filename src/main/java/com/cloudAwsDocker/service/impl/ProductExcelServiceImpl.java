package com.cloudAwsDocker.service.impl;

import com.cloudAwsDocker.dto.ExcelImportResponse;
import com.cloudAwsDocker.entity.Product;
import com.cloudAwsDocker.exception.DataValidationException;
import com.cloudAwsDocker.exception.ExcelProcessingException;
import com.cloudAwsDocker.exception.InvalidFileFormatException;
import com.cloudAwsDocker.repository.ProductRepository;
import com.cloudAwsDocker.service.ProductExcelService;
import com.cloudAwsDocker.util.ExcelHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.cloudAwsDocker.entity.UploadedFile;
import com.cloudAwsDocker.repository.UploadedFileRepository;
import org.springframework.beans.factory.annotation.Value;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductExcelServiceImpl implements ProductExcelService {
    private final ProductRepository productRepository;
    private final UploadedFileRepository uploadedFileRepository;

    @Value("${file.upload-dir:${user.home}/Downloads}")
    private String uploadDir;

    @Override
    public ExcelImportResponse importProducts(MultipartFile file) {
        log.info("Starting Excel import for file: {}", file.getOriginalFilename());

        // Save file info to database
        UploadedFile uploadedFile = saveFileMetadata(file);

        try {
            if (!ExcelHelper.hasExcelFormat(file)) {
                log.error("Invalid file format: {}", file.getContentType());
                uploadedFile.setStatus(UploadedFile.FileStatus.FAILED);
                uploadedFileRepository.save(uploadedFile);
                throw new InvalidFileFormatException("Please upload an Excel file");
            }

            // Save file to filesystem
            String filePath = saveFileToDisk(file, uploadedFile);
            uploadedFile.setFilePath(filePath);
            uploadedFile.setStatus(UploadedFile.FileStatus.PROCESSING);
            uploadedFileRepository.save(uploadedFile);

            List<Product> products = ExcelHelper.excelToProducts(file.getInputStream());
            log.debug("Parsed {} products from Excel file", products.size());

            var response = new ExcelImportResponse(products.size());
            response.setFileId(uploadedFile.getId());
            response.setFilePath(filePath);

            var errorMessages = new ArrayList<String>();
            int successfulImports = 0;
            int failedImports = 0;

            for (int i = 0; i < products.size(); i++) {
                Product product = products.get(i);
                int rowNumber = i + 2; // +2 for header and 0-based index

                try {
                    validateProduct(product, rowNumber);

                    if (product.getId() != null) {
                        processExistingProduct(product, rowNumber, errorMessages);
                    } else {
                        processNewProduct(product, rowNumber, errorMessages);
                    }

                    successfulImports++;
                    log.debug("Successfully processed product at row {}: {}", rowNumber, product.getName());
                } catch (DataValidationException e) {
                    errorMessages.add(e.getMessage());
                    failedImports++;
                    log.warn("Validation failed for row {}: {}", rowNumber, e.getMessage());
                }
            }

            response.setSuccessfulImports(successfulImports);
            response.setFailedImports(failedImports);
            response.setErrorMessages(errorMessages);

            // Update file status
            uploadedFile.setProcessedRecords(successfulImports);
            uploadedFile.setFailedRecords(failedImports);
            uploadedFile.setStatus(UploadedFile.FileStatus.COMPLETED);
            uploadedFileRepository.save(uploadedFile);

            log.info("Excel import completed. Successful: {}, Failed: {}", successfulImports, failedImports);
            return response;
        } catch (IOException e) {
            // Update file status to failed
            uploadedFile.setStatus(UploadedFile.FileStatus.FAILED);
            uploadedFileRepository.save(uploadedFile);

            log.error("Failed to process Excel file: {}", e.getMessage(), e);
            throw new ExcelProcessingException("Failed to process Excel file: " + e.getMessage());
        }
    }

    private UploadedFile saveFileMetadata(MultipartFile file) {
        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setOriginalFilename(file.getOriginalFilename());
        uploadedFile.setFilename(generateUniqueFilename(file.getOriginalFilename()));
        uploadedFile.setFileSize(file.getSize());
        uploadedFile.setContentType(file.getContentType());
        uploadedFile.setTotalRecords(0);
        uploadedFile.setProcessedRecords(0);
        uploadedFile.setFailedRecords(0);
        uploadedFile.setStatus(UploadedFile.FileStatus.UPLOADED);

        return uploadedFileRepository.save(uploadedFile);
    }

    private String saveFileToDisk(MultipartFile file, UploadedFile uploadedFile) throws IOException {
        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String filename = uploadedFile.getFilename();
        Path filePath = uploadPath.resolve(filename);

        // Save file
        Files.copy(file.getInputStream(), filePath);

        return filePath.toString();
    }

    private String generateUniqueFilename(String originalFilename) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);

        if (originalFilename != null && originalFilename.contains(".")) {
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            return "products_" + timestamp + "_" + uuid + extension;
        }

        return "products_" + timestamp + "_" + uuid + ".xlsx";
    }

    private void processExistingProduct(Product product, int rowNumber, List<String> errorMessages) {
        productRepository.findById(product.getId()).ifPresentOrElse(
                existingProduct -> {
                    updateProductFields(existingProduct, product);
                    productRepository.save(existingProduct);
                    log.debug("Updated existing product with ID: {}", product.getId());
                },
                () -> {
                    String errorMsg = "Row " + rowNumber + ": Product with ID " + product.getId() + " not found for update";
                    errorMessages.add(errorMsg);
                    log.warn(errorMsg);
                }
        );
    }

    private void processNewProduct(Product product, int rowNumber, List<String> errorMessages) {
        if (productRepository.findByName(product.getName()).isPresent()) {
            String errorMsg = "Row " + rowNumber + ": Product with name '" + product.getName() + "' already exists";
            errorMessages.add(errorMsg);
            log.warn(errorMsg);
        } else {
            productRepository.save(product);
            log.debug("Created new product: {}", product.getName());
        }
    }

    @Override
    public ByteArrayInputStream exportProducts() {
        log.info("Exporting all products to Excel");
        List<Product> products = productRepository.findAll();
        log.debug("Found {} products for export", products.size());
        return ExcelHelper.productsToExcel(products);
    }

    @Override
    public ByteArrayInputStream downloadTemplate() {
        log.info("Generating Excel template");
        return ExcelHelper.productsToExcel(Collections.emptyList());
    }

    private void updateProductFields(Product existing, Product updated) {
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setPrice(updated.getPrice());
        existing.setQuantity(updated.getQuantity());
    }

    private void validateProduct(Product product, int rowNumber) {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new DataValidationException("Row " + rowNumber + ": Product name is required");
        }

        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DataValidationException("Row " + rowNumber + ": Product price must be greater than 0");
        }

        if (product.getQuantity() == null || product.getQuantity() < 0) {
            throw new DataValidationException("Row " + rowNumber + ": Product quantity cannot be negative");
        }
    }
}