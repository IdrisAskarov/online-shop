package com.codergm.orderservice.service;

import com.codergm.orderservice.model.OrderRequest;
import com.codergm.orderservice.model.OrderResponse;

public interface OrderService {
    Long placeOrder(OrderRequest orderRequest);

    OrderResponse getOrderDetails(Long orderId);
}
