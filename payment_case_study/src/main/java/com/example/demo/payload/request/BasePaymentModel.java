package com.example.demo.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BasePaymentModel implements PaymentModel {
    private BigDecimal amount;
    private Long userId;
    private String device;
    private String paymentType;
    private String requestId;
}
