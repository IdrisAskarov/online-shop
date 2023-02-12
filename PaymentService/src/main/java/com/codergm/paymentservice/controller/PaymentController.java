package com.codergm.paymentservice.controller;

import com.codergm.paymentservice.model.PaymentRequest;
import com.codergm.paymentservice.model.PaymentResponse;
import com.codergm.paymentservice.service.PaymentService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("/payment")
@Log4j2
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("")
    public ResponseEntity<Long> doPayment(@RequestBody PaymentRequest paymentRequest) {
        log.info("do payment for order id {}", paymentRequest.getOrderId());
        return status(HttpStatus.OK).body(paymentService.doPayment(paymentRequest));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentDetailsByOrderId(@PathVariable Long orderId){
        return status(HttpStatus.OK)
                .body(paymentService.getPaymentDetailsByOrderId(orderId));
    }
}
