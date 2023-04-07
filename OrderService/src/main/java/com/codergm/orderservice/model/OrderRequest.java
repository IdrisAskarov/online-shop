package com.codergm.orderservice.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderRequest {
    private Long productId;
    private Double totalAmount;
    private Integer quantity;
    private PaymentMode paymentMode;
}
