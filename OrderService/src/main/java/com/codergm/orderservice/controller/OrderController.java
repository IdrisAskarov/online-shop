package com.codergm.orderservice.controller;

import com.codergm.orderservice.entity.Order;
import com.codergm.orderservice.model.OrderRequest;
import com.codergm.orderservice.model.OrderResponse;
import com.codergm.orderservice.service.OrderSevice;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("/order")
@Log4j2
public class OrderController {

    private final OrderSevice orderSevice;

    public OrderController(OrderSevice orderSevice) {
        this.orderSevice = orderSevice;
    }

    @PostMapping("/place-order")
    public ResponseEntity<Long> placeOrder(@RequestBody OrderRequest orderRequest) {
        log.info("create a new order");
        Long orderId = orderSevice.placeOrder(orderRequest);
        log.info("The order with id {} was placed", orderId);
        return status(HttpStatus.CREATED).body(orderId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderDetailsById(@PathVariable("id") Long orderId) {
        OrderResponse orderResponse = orderSevice.getOrderDetails(orderId);
        return status(HttpStatus.OK).body(orderResponse);
    }

}
