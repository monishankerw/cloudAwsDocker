package com.cloudAwsDocker.repository;

import com.cloudAwsDocker.entity.FileMetadata;
import com.cloudAwsDocker.enums.FileStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata, String> {
    Optional<FileMetadata> findByIdAndStatus(String id, FileStatus status);
    List<FileMetadata> findByStatus(FileStatus status);
    boolean existsByStoragePath(String storagePath);
}
