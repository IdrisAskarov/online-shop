package com.codergm.orderservice.external.request;

import com.codergm.orderservice.model.PaymentMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentRequest {
    private Long orderId;
    private Double amount;
    private String referenceNumber;
    private PaymentMode paymentMode;
}
