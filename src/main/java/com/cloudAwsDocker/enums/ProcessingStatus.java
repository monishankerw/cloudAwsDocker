package com.cloudAwsDocker.enums;

/**
 * Enum representing the processing status of Excel data
 */
public enum ProcessingStatus {
    /**
     * The data is queued for processing
     */
    PENDING,
    
    /**
     * The data is currently being processed
     */
    PROCESSING,
    
    /**
     * The data has been successfully processed
     */
    PROCESSED,
    
    /**
     * The data processing failed
     */
    FAILED,
    
    /**
     * The data processing was completed with some warnings
     */
    COMPLETED_WITH_WARNINGS,
    
    /**
     * The data processing was cancelled
     */
    CANCELLED
}
