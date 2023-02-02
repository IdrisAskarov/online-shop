package com.codergm.orderservice.service;

import com.codergm.orderservice.model.OrderRequest;

public interface OrderSevice {
    Long placeOrder(OrderRequest orderRequest);
}
