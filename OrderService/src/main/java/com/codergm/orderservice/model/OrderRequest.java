package com.codergm.orderservice.model;

import lombok.Data;

@Data
public class OrderRequest {
    private Long productId;
    private Double totalAmount;
    private Integer quantity;
    private PaymentMode paymentMode;
}
