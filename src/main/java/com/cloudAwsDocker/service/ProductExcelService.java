package com.cloudAwsDocker.service;

import com.cloudAwsDocker.dto.ExcelImportResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;

public interface ProductExcelService {
    ExcelImportResponse importProducts(MultipartFile file);
    ByteArrayInputStream exportProducts();
    ByteArrayInputStream downloadTemplate();
}