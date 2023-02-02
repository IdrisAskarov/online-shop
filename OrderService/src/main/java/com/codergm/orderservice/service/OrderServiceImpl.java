package com.codergm.orderservice.service;

import com.codergm.orderservice.entity.Order;
import com.codergm.orderservice.model.OrderRequest;
import com.codergm.orderservice.repository.OrderRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Log4j2
public class OrderServiceImpl implements OrderSevice {

    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Long placeOrder(OrderRequest orderRequest) {
        log.info("Placing order request: {}", orderRequest);
        Order order = Order
                .builder()
                .amount(orderRequest.getTotalAmount())
                .orderStatus("CREATED")
                .productId(orderRequest.getProductId())
                .quantity(orderRequest.getQuantity())
                .orderDate(Instant.now())
                .build();
        orderRepository.save(order);
        log.info("The order placed successfully with order id: {}", order.getId());
        return order.getId();
    }
}
