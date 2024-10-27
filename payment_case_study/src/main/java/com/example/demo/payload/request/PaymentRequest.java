package com.example.demo.payload.request;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest extends BasePaymentModel{
    @Nullable
    private String cardNumber;
    @Nullable
    private String cardHolderName;
    @Nullable
    private String expirationDate;
    @Nullable
    private String cvv;
    @Nullable
    private String sourceAccountId;
    @Nullable
    private String sourceAccountCode;
    @Nullable
    private String sourceAccountNumber;
    @Nullable
    private String sourceAccountHolderName;
    @Nullable
    private String reference;
    @Nullable
    private String transactionId;
    @Nullable
    private String transactionPin;
    @Nullable
    private LocalDate transactionDate;
    @Nullable
    private String beneficiaryAccountNumber;
    @Nullable
    private String beneficiaryBankHolderName;
}
