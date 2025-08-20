package com.cloudAwsDocker.util;

import com.cloudAwsDocker.entity.Product;
import com.cloudAwsDocker.exception.ExcelProcessingException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class ExcelHelper {
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String[] HEADERs = { "Id", "Name", "Description", "Price", "Quantity" };
    static String SHEET = "Products";

    public static boolean hasExcelFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType());
    }

    public static ByteArrayInputStream  productsToExcel(List<Product> products) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(SHEET);
            
            // Header
            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < HEADERs.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(HEADERs[col]);
            }
            
            // Data
            int rowIdx = 1;
            for (Product product : products) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(product.getId());
                row.createCell(1).setCellValue(product.getName());
                row.createCell(2).setCellValue(product.getDescription());
                row.createCell(3).setCellValue(product.getPrice().doubleValue());
                row.createCell(4).setCellValue(product.getQuantity());
            }
            
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Failed to export data to Excel: " + e.getMessage());
        }
    }

    public static List<Product> excelToProducts(InputStream is) {
        try (Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheet(SHEET);
            if (sheet == null) {
                throw new ExcelProcessingException("Sheet 'Products' not found");
            }
            
            Iterator<Row> rows = sheet.iterator();
            List<Product> products = new ArrayList<>();
            
            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();
                
                // Skip header
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }
                
                Iterator<Cell> cellsInRow = currentRow.iterator();
                Product product = new Product();
                
                int cellIdx = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();
                    
                    switch (cellIdx) {
                        case 0:
                            if (currentCell.getCellType() == CellType.NUMERIC) {
                                product.setId((long) currentCell.getNumericCellValue());
                            }
                            break;
                        case 1:
                            product.setName(currentCell.getStringCellValue());
                            break;
                        case 2:
                            product.setDescription(currentCell.getStringCellValue());
                            break;
                        case 3:
                            if (currentCell.getCellType() == CellType.NUMERIC) {
                                product.setPrice(BigDecimal.valueOf(currentCell.getNumericCellValue()));
                            }
                            break;
                        case 4:
                            if (currentCell.getCellType() == CellType.NUMERIC) {
                                product.setQuantity((int) currentCell.getNumericCellValue());
                            }
                            break;
                        default:
                            break;
                    }
                    cellIdx++;
                }
                products.add(product);
            }
            return products;
        } catch (IOException e) {
            throw new ExcelProcessingException("Failed to parse Excel file: " + e.getMessage());
        }
    }
}