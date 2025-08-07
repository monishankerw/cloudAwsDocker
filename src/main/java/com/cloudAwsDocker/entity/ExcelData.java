package com.cloudAwsDocker.entity;


import com.cloudAwsDocker.enums.ProcessingStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "excel_data")
public class ExcelData {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @Column(nullable = false)
    private String fileName;

    @Column(name = "sheet_name")
    private String sheetName;

    @Column(name = "row_num")
    private int rowNumber;

    @Column(name = "column_name")
    private String columnName;

    @Column(name = "cell_value", columnDefinition = "TEXT")
    private String cellValue;

    @Enumerated(EnumType.STRING)
    private ProcessingStatus status = ProcessingStatus.PENDING;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "processed_by")
    private String processedBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Reference to the original file
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_metadata_id")
    private FileMetadata fileMetadata;
}
