package com.cloudAwsDocker.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelSampleCreator {
    public static void main(String[] args) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Products");

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Id", "Name", "Description", "Price", "Quantity"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Sample data
        Object[][] data = {
                {1, "Laptop", "High-end gaming", 1200, 10},
                {2, "Smartphone", "Latest model", 800, 25},
                {3, "Headphones", "Wireless", 150, 50},
                {4, "Keyboard", "Mechanical", 100, 30},
                {5, "Monitor", "27-inch 4K", 400, 15}
        };

        int rowNum = 1;
        for (Object[] rowData : data) {
            Row row = sheet.createRow(rowNum++);
            int colNum = 0;
            for (Object field : rowData) {
                Cell cell = row.createCell(colNum++);
                if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Integer) {
                    cell.setCellValue((Integer) field);
                } else if (field instanceof Double) {
                    cell.setCellValue((Double) field);
                }
            }
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // ✅ Save file directly to Downloads folder on Mac
        String filePath = System.getProperty("user.home") + "/Downloads/sample_products.xlsx";
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
            System.out.println("✅ Excel file created at: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}