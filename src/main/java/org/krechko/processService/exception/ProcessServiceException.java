package org.krechko.processService.exception;

public class ProcessServiceException extends RuntimeException {
    public ProcessServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
