package com.example.demo.service.paymentService;

import com.example.demo.config.PaymentTypeConfig;
import com.example.demo.constants.ResponseStatus;
import com.example.demo.constants.StringValues;
import com.example.demo.entity.Transaction;
import com.example.demo.exceptions.BadRequestException;
import com.example.demo.factory.PaymentServiceFactory;
import com.example.demo.payload.BaseResponse;
import com.example.demo.payload.request.CardPaymentModel;
import com.example.demo.payload.request.PaymentModel;
import com.example.demo.payload.request.PaymentRequest;
import com.example.demo.payload.request.TransferPaymentModel;
import com.example.demo.payload.response.PaymentResponse;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.service.deviceService.DeviceService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl {

    private final PaymentTypeConfig paymentTypeConfig;
    private final PaymentServiceFactory<?> paymentServiceFactory;
    private final TransactionRepository transactionRepository;

    @Transactional
    public BaseResponse<PaymentResponse> processPayment(HttpServletRequest request, PaymentRequest paymentRequest) {
        BaseResponse<PaymentResponse> response = new BaseResponse<>();
        String url = request.getRequestURL().toString();
        if (paymentRequest.getPaymentType() != null && !paymentRequest.getPaymentType().isEmpty()) {
            paymentRequest.setPaymentType(paymentRequest.getPaymentType().toUpperCase());
        }

        // Validate Device
        processDeviceValidation(paymentRequest);

        Optional<Transaction> transaction = transactionRepository.findTransactionByRequestId(paymentRequest.getRequestId());
        if (transaction.isPresent()) {
            response.setCode(ResponseStatus.FAILED.getCode());
            response.setStatus(ResponseStatus.FAILED.getStatus());
            response.setMessage(StringValues.DUPLICATE_REQUEST_ID);
            response.setData(null);
            log.info(StringValues.LOGGER_STRING_POST, url, paymentRequest, response);
            return response;
        }

        if(paymentTypeConfig.getTypes().contains(paymentRequest.getPaymentType())) {
            response = processPaymentType(paymentRequest);
            log.info(StringValues.LOGGER_STRING_POST, url, paymentRequest, response);
            return response;
        }

        response.setCode(ResponseStatus.FAILED.getCode());
        response.setStatus(ResponseStatus.FAILED.getStatus());
        response.setMessage(StringValues.INVALID_PAYMENT_TYPE);
        response.setData(null);
        log.info(StringValues.LOGGER_STRING_POST, url, paymentRequest, response);
        return response;
    }

    public BaseResponse<List<PaymentResponse>> getPayment(HttpServletRequest request, int pageNumber, int pageSize, Long userId) {
        BaseResponse<List<PaymentResponse>> response = new BaseResponse<>();
        String url = request.getRequestURL().toString();

        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        Page<Transaction> transactions = transactionRepository.findAllByUserId(userId, pageRequest);
        log.info("transactions --> {}", transactions);
        if (Optional.ofNullable(transactions).isEmpty()) {
            response.setCode(ResponseStatus.FAILED.getCode());
            response.setStatus(ResponseStatus.FAILED.getStatus());
            response.setMessage(StringValues.NO_RECORD_FOUND);
            response.setData(null);
            log.info(StringValues.LOGGER_STRING_POST, url, userId, response);
            return response;
        }

        List<PaymentResponse> paymentResponseList = transactions.map(this::mapTransactionToPaymentResponse)
                .get()
                .collect(Collectors.toList());
        log.info("paymentResponseList ---> {}", paymentResponseList);

        response.setCode(ResponseStatus.SUCCESS.getCode());
        response.setStatus(ResponseStatus.SUCCESS.getStatus());
        response.setMessage(StringValues.RECORD_FOUND);
        response.setData(paymentResponseList);
        log.info(StringValues.LOGGER_STRING_POST, url, userId, response);
        return response;
    }

    private PaymentResponse mapTransactionToPaymentResponse(Transaction transaction) {
        return PaymentResponse.builder()
                .paymentType(transaction.getPaymentType())
                .transactionRef(transaction.getTransactionRef())
                .amount(transaction.getAmount())
                .build();
    }

    private BaseResponse<PaymentResponse> processPaymentType(PaymentRequest paymentRequest) {
        BaseResponse<PaymentResponse> response = new BaseResponse<>();
        PaymentService<PaymentModel> paymentService = (PaymentService<PaymentModel>) paymentServiceFactory
                .retrievePaymentService(paymentRequest.getPaymentType());
        PaymentModel paymentModel = buildPaymentModel(paymentRequest.getPaymentType(), paymentRequest);
        if (paymentModel == null) {
            response = new BaseResponse<>();
            response.setCode(ResponseStatus.FAILED.getCode());
            response.setStatus(ResponseStatus.FAILED.getStatus());
            response.setMessage(StringValues.INVALID_PAYMENT_TYPE);
            response.setData(null);
            return response;
        }
        PaymentResponse paymentResponse = paymentService.makePayment(paymentModel);
        response.setCode(ResponseStatus.SUCCESS.getCode());
        response.setStatus(ResponseStatus.SUCCESS.getStatus());
        response.setMessage(StringValues.PAYMENT_PROCESS_SUCCESSFUL);
        response.setData(paymentResponse);

        return response;
    }

    private PaymentModel buildPaymentModel(final String paymentMethod, final PaymentRequest paymentRequest) {
        switch (paymentMethod.toLowerCase()) {
            case "card":
                return buildCardPaymentModel(paymentRequest);
            case "bank_transfer":
                return buildBankTransferPaymentModel(paymentRequest);
            default:
                return null;
        }
    }

    private TransferPaymentModel buildBankTransferPaymentModel(PaymentRequest paymentRequest) {
        TransferPaymentModel model = new TransferPaymentModel();
        BeanUtils.copyProperties(paymentRequest, model);

        // Todo: Validate necessary fields
        return model;
    }

    private CardPaymentModel buildCardPaymentModel(PaymentRequest paymentRequest) {
        CardPaymentModel model = new CardPaymentModel();
        BeanUtils.copyProperties(paymentRequest, model);

        String validateModel = model.validateCardPaymentFields();
        if (!validateModel.equals(StringValues.VALIDATION_SUCCESS)) {
            throw new BadRequestException(ResponseStatus.INVALID_FIELDS_PROVIDED.getCode(), validateModel);
        }
        return model;
    }

    private void processDeviceValidation(PaymentRequest paymentRequest) {
        DeviceService deviceService = paymentServiceFactory.retrieveDevice(paymentRequest.getDevice());
        deviceService.validateDevice();
    }

}
