package com.codergm.orderservice.external.client;

import com.codergm.orderservice.external.request.PaymentRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service/payment/")
public interface PaymentService {

    @PostMapping("")
    public ResponseEntity<Long> doPayment(@RequestBody PaymentRequest paymentRequest);
}
