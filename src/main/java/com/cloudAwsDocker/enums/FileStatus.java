package com.cloudAwsDocker.enums;

public enum FileStatus {
    ACTIVE,         // File is active and accessible
    DELETED,        // File has been marked as deleted
    PROCESSING,     // File is being processed
    FAILED,         // File processing failed
    QUARANTINED     // File is quarantined due to security concerns
}
