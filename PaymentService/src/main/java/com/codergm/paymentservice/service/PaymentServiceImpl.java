package com.codergm.paymentservice.service;

import com.codergm.paymentservice.entity.TransactionDetails;
import com.codergm.paymentservice.model.PaymentRequest;
import com.codergm.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Log4j2
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Override
    public Long doPayment(PaymentRequest paymentRequest) {
        log.info("Recording payment details: {}", paymentRequest);
        TransactionDetails transactionDetails =
                TransactionDetails.builder()
                        .orderId(paymentRequest.getOrderId())
                        .amount(paymentRequest.getAmount())
                        .paymentMode(paymentRequest.getPaymentMode().name())
                        .paymentStatus("SUCCESS")
                        .referenceNumber(paymentRequest.getReferenceNumber())
                        .paymentDate(Instant.now())
                        .build();
        paymentRepository.save(transactionDetails);
        log.info("Transaction completed with id: {}"+transactionDetails.getId());
        return transactionDetails.getId();
    }
}
