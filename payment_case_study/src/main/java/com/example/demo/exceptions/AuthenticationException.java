package com.example.demo.exceptions;

public class AuthenticationException extends RuntimeException {
    private final String code;
    public AuthenticationException(String message, String code) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
