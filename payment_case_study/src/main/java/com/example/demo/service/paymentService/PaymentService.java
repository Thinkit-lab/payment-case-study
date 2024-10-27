package com.example.demo.service.paymentService;

import com.example.demo.payload.request.PaymentModel;
import com.example.demo.payload.response.PaymentResponse;

public interface PaymentService<T extends PaymentModel> {
    PaymentResponse makePayment(T request);
    String getPaymentMethod();
}
