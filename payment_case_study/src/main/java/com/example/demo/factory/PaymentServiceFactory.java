package com.example.demo.factory;

import com.example.demo.constants.ResponseStatus;
import com.example.demo.constants.StringValues;
import com.example.demo.exceptions.BadRequestException;
import com.example.demo.payload.request.PaymentModel;
import com.example.demo.service.deviceService.DeviceService;
import com.example.demo.service.paymentService.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentServiceFactory<T> {
    private final List<PaymentService<? extends PaymentModel>> paymentServices;
    private final List<DeviceService> deviceServices;

    public PaymentService<? extends PaymentModel> retrievePaymentService(final String paymentMethod) {
        Optional<PaymentService<? extends PaymentModel>> paymentService = paymentServices.stream()
                .filter(service -> service.getPaymentMethod().equals(paymentMethod)).findFirst();

        if (paymentService.isEmpty()) {
            throw new BadRequestException(ResponseStatus.INVALID_REQUEST.getCode(), StringValues.UNSUPPORTED_PAYMENT_METHOD);
        }
        return paymentService.get();
    }

    public DeviceService retrieveDevice(final String device) {
        Optional<DeviceService> deviceService = deviceServices.stream()
                .filter(service -> service.getDeviceType().equals(device)).findFirst();

        if (deviceService.isEmpty()) {
            throw new BadRequestException(ResponseStatus.INVALID_REQUEST.getCode(), StringValues.UNSUPPORTED_DEVICE);
        }
        return deviceService.get();
    }
}
