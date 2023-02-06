package com.codergm.paymentservice.service;

import com.codergm.paymentservice.model.PaymentRequest;

public interface PaymentService {
    Long doPayment(PaymentRequest paymentRequest);
}
