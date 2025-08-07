package com.cloudAwsDocker.repository;

import com.cloudAwsDocker.entity.ExcelData;
import com.cloudAwsDocker.enums.ProcessingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExcelDataRepository extends JpaRepository<ExcelData, String> {

    Page<ExcelData> findByFileName(String fileName, Pageable pageable);

    List<ExcelData> findByStatus(ProcessingStatus status);

    long countByFileName(String fileName);

    long countByStatus(ProcessingStatus status);

    boolean existsByFileName(String fileName);
}
