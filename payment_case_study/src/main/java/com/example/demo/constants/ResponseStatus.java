package com.example.demo.constants;

import lombok.Getter;

@Getter
public enum ResponseStatus {

    SUCCESS("Success", "MwI00"),
    FAILED("Failed", "MwF00"),
    INVALID_REQUEST("Invalid request", "MwE00"),
    GENERAL_ERROR("An error occurred", "MwG00"),
    ENCRYPTION_ERROR("Encryption error", "MwG01"),
    INVALID_FIELDS_PROVIDED("Invalid fields provided", "MwE01"),
    AUTHENTICATION_ERROR("Authentication failed", "MwG02"),
    RESOURCE_NOT_FOUND("Resource not found", "Mw400"),
    EVENT_PUBLISH_ERROR("Error while publishing event", "MwE02"),
    EVENT_LISTENER_ERROR("Error while processing event", "MwE03"),
    GATEWAY_ERROR("Error occured while processing payment", "MwG01"),
    PAYMENT_ERROR("Payment failed", "MwP00"),
    ;

    private final String status;
    private final String code;

    ResponseStatus(String status, String code) {
        this.status = status;
        this.code = code;
    }

}
