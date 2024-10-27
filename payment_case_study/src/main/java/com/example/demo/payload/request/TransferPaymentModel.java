package com.example.demo.payload.request;

import lombok.*;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferPaymentModel extends BasePaymentModel{
    private String sourceAccountId;
    private String sourceAccountCode;
    private String sourceAccountNumber;
    private String sourceAccountHolderName;
    private BeneficiaryAccount beneficiaryAccount;
    private String reference;
    private String transactionId;
    private String transactionPin;
    private LocalDate transactionDate;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BeneficiaryAccount {
        private String id;
        private String beneficiaryId;
        private String accountId;
        private String accountNumber;
        private String bankHolderName;
        private String mobileNumber;
        private String code;
        private String emailAddress;
    }
}
