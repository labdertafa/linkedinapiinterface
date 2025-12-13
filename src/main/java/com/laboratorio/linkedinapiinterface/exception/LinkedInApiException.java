package com.laboratorio.linkedinapiinterface.exception;

/**
 *
 * @author Rafael
 * @version 1.2
 * @created 10/07/2024
 * @updated 13/12/2025
 */
public class LinkedInApiException extends RuntimeException {
    private final Throwable causaOriginal;
    
    public LinkedInApiException(String message) {
        super(message);
        this.causaOriginal = null;
    }

    public LinkedInApiException(String message, Throwable causaOriginal) {
        super(message, causaOriginal);
        this.causaOriginal = causaOriginal;
    }
    
    @Override
    public String getMessage() {
        if (this.causaOriginal != null) {
            return super.getMessage() + " | Causa original: " + this.causaOriginal.getMessage();
        }
        
        return super.getMessage();
    }
}