package com.example.demo.exceptions;

public class EventException extends RuntimeException{
    private final String code;

    public EventException(String code, String message){
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
