package com.eainde.synapse.forms.exception;

import java.util.List;

/**
 * Custom unchecked exception for all library errors.
 */
public class RenderingException extends RuntimeException {

    private final String errorCode;
    private final List<ValidationError> details;

    public RenderingException(String message, String errorCode, List<ValidationError> details) {
        super(message);
        this.errorCode = errorCode;
        this.details = details;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public List<ValidationError> getDetails() {
        return details;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " (Code: " + errorCode + ", Details: " + details + ")";
    }
}
