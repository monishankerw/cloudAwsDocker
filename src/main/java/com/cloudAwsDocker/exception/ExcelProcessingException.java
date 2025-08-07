package com.cloudAwsDocker.exception;

/**
 * Exception thrown when there is an error processing Excel files.
 * This is a runtime exception that can be used to wrap lower-level exceptions
 * and provide more context about what went wrong during Excel processing.
 */
public class ExcelProcessingException extends RuntimeException {

    /**
     * Constructs a new ExcelProcessingException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the getMessage() method).
     */
    public ExcelProcessingException(String message) {
        super(message);
    }

    /**
     * Constructs a new ExcelProcessingException with the specified detail message
     * and cause.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the getMessage() method).
     * @param cause   the cause (which is saved for later retrieval by the
     *                getCause() method). (A null value is permitted, and indicates
     *                that the cause is nonexistent or unknown.)
     */
    public ExcelProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new ExcelProcessingException with the specified cause and a
     * detail message of (cause==null ? null : cause.toString()) (which
     * typically contains the class and detail message of cause).
     *
     * @param cause the cause (which is saved for later retrieval by the
     *              getCause() method). (A null value is permitted, and indicates
     *              that the cause is nonexistent or unknown.)
     */
    public ExcelProcessingException(Throwable cause) {
        super(cause);
    }
}
