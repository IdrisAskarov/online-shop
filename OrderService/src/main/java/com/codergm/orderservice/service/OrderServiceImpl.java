package com.codergm.orderservice.service;



import com.codergm.orderservice.entity.Order;
import com.codergm.orderservice.exception.OrderException;
import com.codergm.orderservice.external.client.PaymentService;
import com.codergm.orderservice.external.client.ProductService;
import com.codergm.orderservice.model.OrderRequest;
import com.codergm.orderservice.model.OrderResponse;
import com.codergm.orderservice.model.PaymentRequest;
import com.codergm.orderservice.model.ProductResponse;
import com.codergm.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
@Log4j2
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderSevice {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final PaymentService paymentService;

    private final RestTemplate restTemplate;

    @Override
    public Long placeOrder(OrderRequest orderRequest) {
        log.info("Placing order request: {}", orderRequest);
        productService.reduceQuantity(orderRequest.getProductId(), orderRequest.getQuantity());
        log.info("Creating order with status CREATED");
        Order order = Order
                .builder()
                .amount(orderRequest.getTotalAmount())
                .orderStatus("CREATED")
                .productId(orderRequest.getProductId())
                .quantity(orderRequest.getQuantity())
                .orderDate(Instant.now())
                .build();
        orderRepository.save(order);

        log.info("Calling Payment Service to complete the payment");

        String orderStatus = null;
        try {
            paymentService.doPayment(PaymentRequest
                    .builder()
                    .orderId(order.getId())
                    .paymentMode(orderRequest.getPaymentMode())
                    .amount(orderRequest.getTotalAmount())
                    .build());
            log.info("Payment done Successfull. Changing the order status to PLACED");
            orderStatus = "PLACED";
        } catch (Exception e) {
            log.error("Error occured in payment. Changing order status to PAYMENT_FAILEd");
            orderStatus = "PAYMENT_FAILEd";
        }

        order.setOrderStatus(orderStatus);
        orderRepository.save(order);

        log.info("The order placed successfully with order id: {}", order.getId());
        return order.getId();
    }

    @Override
    public OrderResponse getOrderDetails(Long orderId) {
        log.info("Get order details for Order Id: { }", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException("Order By orderID"+orderId+" not found","ORDER_NOT_FOUND"));
        log.info("Invoking Product Service to fetch the product for id: {}"+orderId);
        ProductResponse producerResponse =
                restTemplate.getForObject("http://PRODUCT-SERVICE/product/"+order.getProductId(),
                        ProductResponse.class);
        OrderResponse.ProductDetails productDetails = new OrderResponse.ProductDetails();
        BeanUtils.copyProperties(producerResponse,productDetails);
        OrderResponse orderResponse =
                OrderResponse.builder()
                        .orderId(orderId)
                        .orderStatus(order.getOrderStatus())
                        .amount(order.getAmount())
                        .orderDate(order.getOrderDate())
                        .productDetails(productDetails)
                        .build();
        return orderResponse;
    }
}
