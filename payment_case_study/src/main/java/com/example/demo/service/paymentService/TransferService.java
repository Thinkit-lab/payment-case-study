package com.example.demo.service.paymentService;

import com.example.demo.constants.PaymentType;
import com.example.demo.entity.Transaction;
import com.example.demo.exceptions.GeneralException;
import com.example.demo.integrations.PaymentGatewayMock;
import com.example.demo.payload.request.TransferPaymentModel;
import com.example.demo.payload.response.PaymentResponse;
import com.example.demo.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.example.demo.constants.ResponseStatus.GATEWAY_ERROR;
import static com.example.demo.constants.ResponseStatus.PAYMENT_ERROR;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferService implements PaymentService<TransferPaymentModel> {
    private final TransactionRepository transactionRepository;
    private final PaymentGatewayMock paymentGatewayMock;


    @Override
    public PaymentResponse makePayment(TransferPaymentModel request) {
        // Todo: Do all necessary validation base on the payment type and Make a call to the mocked payment gateway
        String transRef = UUID.randomUUID().toString();
        boolean isPaymentSuccessful;
        try {
            isPaymentSuccessful = paymentGatewayMock.processBankTransfer(request);
            if (!isPaymentSuccessful) {
                log.info("Transfer Payment failed for transaction with ref: {}", transRef);
                throw new GeneralException(PAYMENT_ERROR.getCode(), PAYMENT_ERROR.getStatus());
            }

            log.info("Transfer Payment successful for transaction with ref: {}", transRef);
            Transaction transaction = new Transaction();
            BeanUtils.copyProperties(request, transaction);
            transaction.setTransactionRef(transRef);
            transactionRepository.save(transaction);
            return PaymentResponse.builder()
                    .paymentType(transaction.getPaymentType())
                    .amount(transaction.getAmount())
                    .transactionRef(transRef)
                    .build();
        } catch (Exception ex) {
            log.error("Error occurred while processing payment", ex);
            throw new GeneralException(GATEWAY_ERROR.getCode(), GATEWAY_ERROR.getStatus());
        }
    }

    @Override
    public String getPaymentMethod() {
        return PaymentType.BANK_TRANSFER.name();
    }
}
