package com.codergm.paymentservice.service;

import com.codergm.paymentservice.model.PaymentRequest;
import com.codergm.paymentservice.model.PaymentResponse;

public interface PaymentService {
    Long doPayment(PaymentRequest paymentRequest);

    PaymentResponse getPaymentDetailsByOrderId(Long orderId);
}
