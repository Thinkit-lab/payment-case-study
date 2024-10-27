package com.example.demo.integrations;

import com.example.demo.payload.request.CardPaymentModel;
import com.example.demo.payload.request.TransferPaymentModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentGatewayMock {

    public boolean processCardPayment(CardPaymentModel request) {
        return Math.random() < 0.9;
    }

    public boolean processBankTransfer(TransferPaymentModel request) {
        return Math.random() < 0.95;
    }
}
