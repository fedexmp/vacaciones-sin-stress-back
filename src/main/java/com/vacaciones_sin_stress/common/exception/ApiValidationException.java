package com.vacaciones_sin_stress.common.exception;

public class ApiValidationException extends RuntimeException {

    public ApiValidationException(String message) {
        super(message);
    }
}
