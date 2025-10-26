package com.eainde.synapse.forms.exception;

/**
 * A detailed error message, including a JSON pointer to the failing path.
 */
public record ValidationError(
        String code,
        String message,
        String jsonPointer
) {}
