package com.example.demo.payload.request;

import com.example.demo.constants.StringValues;
import lombok.*;

import java.util.regex.Pattern;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardPaymentModel extends BasePaymentModel {
    private String cardNumber;
    private String cardHolderName;
    private String expirationDate;
    private String cvv;

    public String validateCardPaymentFields() {
        if (!isValidCardNumber(cardNumber)) {
            return "Invalid card number";
        }

        if (!isValidCardHolderName(cardHolderName)) {
            return "Invalid cardholder name";
        }

        if (!isValidExpirationDate(expirationDate)) {
            return "Invalid expiration date";
        }

        if (!isValidCvv(cvv)) {
            return "Invalid CVV";
        }

        return StringValues.VALIDATION_SUCCESS;
    }

    private boolean isValidCardNumber(String cardNumber) {
        return Pattern.matches("\\d{16}", cardNumber);
    }

    private boolean isValidCardHolderName(String cardHolderName) {
        return Pattern.matches("[A-Za-z ]+", cardHolderName);
    }

    private boolean isValidExpirationDate(String expirationDate) {
        return Pattern.matches("\\d{2}/\\d{2}", expirationDate);
    }

    private boolean isValidCvv(String cvv) {
        return Pattern.matches("\\d{3,4}", cvv);
    }
}
