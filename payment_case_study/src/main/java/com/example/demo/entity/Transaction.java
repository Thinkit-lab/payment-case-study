package com.example.demo.entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction extends BaseModel{
    private String transactionRef;
    private BigDecimal amount;
    private Long userId;
    private String device;
    private String paymentType;
    private String requestId;
}
