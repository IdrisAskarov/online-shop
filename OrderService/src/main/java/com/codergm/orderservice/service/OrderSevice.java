package com.codergm.orderservice.service;

import com.codergm.orderservice.model.OrderRequest;
import com.codergm.orderservice.model.OrderResponse;

public interface OrderSevice {
    Long placeOrder(OrderRequest orderRequest);

    OrderResponse getOrderDetails(Long orderId);
}
