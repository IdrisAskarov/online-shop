package com.codergm.orderservice.external.client;

import com.codergm.orderservice.exception.OrderException;
import com.codergm.orderservice.external.request.PaymentRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@CircuitBreaker(name = "external",fallbackMethod = "fallback")
@FeignClient(name = "payment-service/payment/")
public interface PaymentService {

    @PostMapping("")
    ResponseEntity<Long> doPayment(@RequestBody PaymentRequest paymentRequest);

    default ResponseEntity<Long> fallback(PaymentRequest paymentRequest, Exception e){
        throw  new OrderException("Payment Service is not available","INTERNAL_SERVER_ERROR");
    }
}
