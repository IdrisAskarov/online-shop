package com.codergm.orderservice.service;

import com.codergm.orderservice.entity.Order;
import com.codergm.orderservice.exception.OrderException;
import com.codergm.orderservice.external.client.PaymentService;
import com.codergm.orderservice.external.client.ProductService;
import com.codergm.orderservice.external.request.PaymentRequest;
import com.codergm.orderservice.external.response.PaymentResponse;
import com.codergm.orderservice.external.response.ProductResponse;
import com.codergm.orderservice.model.*;
import com.codergm.orderservice.repository.OrderRepository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@SpringBootTest
public class OrderServiceImplTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductService productService;
    @Mock
    private PaymentService paymentService;
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    OrderServiceImpl orderService;

    @Test
    @DisplayName("Get Order - Success Scenario")
    void test_When_Order_Success() {
        //Mocking
        Order order = getMockOrder();

        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

        when(restTemplate.getForObject("http://PRODUCT-SERVICE/product/" + order.getProductId(),
                ProductResponse.class)).thenReturn(getMockProductResponse());

        when(restTemplate.getForObject("http://payment-service/payment/order/" + order.getId(),
                PaymentResponse.class)).thenReturn(getMockPaymentResponse());

        //Actual
        OrderResponse orderResponse = orderService.getOrderDetails(1L);

        //Verification
        verify(orderRepository, times(1)).findById(anyLong());

        verify(restTemplate, times(1))
                .getForObject("http://PRODUCT-SERVICE/product/" + order.getProductId(),
                        ProductResponse.class);

        verify(restTemplate, times(1))
                .getForObject("http://payment-service/payment/order/" + order.getId(),
                        PaymentResponse.class);

        //Assert
        assertNotNull(orderResponse);
        assertEquals(order.getId(), orderResponse.getOrderId());

    }

    @Test
    @DisplayName("Get Order - Failure Scenario")
    void test_When_Get_Order_NOT_FOUND_then_Not_Found() {

        //Mocking
        when(orderRepository.findById(anyLong())).thenReturn(Optional.ofNullable(null));


        //Assert
        OrderException exception = assertThrows(OrderException.class, () -> orderService.getOrderDetails(1L));
        assertEquals("ORDER_NOT_FOUND", exception.getErrorCode());

        //Verify
        verify(orderRepository,times(1)).findById(anyLong());

    }

    @Test
    @DisplayName("Place Order - Success")
    void test_When_Place_Order_Success(){
        Order order = getMockOrder();
        OrderRequest orderRequest = getMockOrderRequest();

        when(orderRepository.save(any(Order.class))).thenReturn(order);


        when(productService.reduceQuantity(anyLong(),anyInt())).thenReturn(new ResponseEntity<Void>(HttpStatus.OK));

        when(paymentService.doPayment(any(PaymentRequest.class)))
                .thenReturn(new ResponseEntity<Long>(1L,HttpStatus.OK));

        long orderId = orderService.placeOrder(orderRequest);

        verify(orderRepository,times(2)).save(any(Order.class));

        verify(productService,times(1)).reduceQuantity(anyLong(),anyInt());

        verify(paymentService,times(1)).doPayment(any(PaymentRequest.class));

        assertEquals(order.getId(), orderId);

    }

    @Test
    @DisplayName("Place Order - Payment Failed Scenario")
    void test_When_Place_Order_Payment_Fails_Then_Order_Placed(){
        Order order = getMockOrder();
        OrderRequest orderRequest = getMockOrderRequest();

        when(orderRepository.save(any(Order.class))).thenReturn(order);

        when(productService.reduceQuantity(anyLong(),anyInt())).thenReturn(new ResponseEntity<Void>(HttpStatus.OK));

        when(paymentService.doPayment(any(PaymentRequest.class)))
                .thenThrow(new RuntimeException());

        long orderId = orderService.placeOrder(orderRequest);

        verify(orderRepository,times(2)).save(any(Order.class));

        verify(productService,times(1)).reduceQuantity(anyLong(),anyInt());

        verify(paymentService,times(1)).doPayment(any(PaymentRequest.class));

        assertEquals(order.getId(), orderId);
    }

    private OrderRequest getMockOrderRequest() {
        return OrderRequest.builder()
                .productId(1L)
                .quantity(100)
                .totalAmount(100.0)
                .paymentMode(PaymentMode.CASH)
                .build();
    }

    private PaymentResponse getMockPaymentResponse() {
        return PaymentResponse.builder()
                .orderId(1L)
                .status(PaymentStatus.ACCEPTED.value)
                .paymentId(1L)
                .paymentMode(PaymentMode.CASH)
                .amount(200.0)
                .paymentDate(Instant.now())
                .build();
    }

    private ProductResponse getMockProductResponse() {
        return ProductResponse.builder()
                .productId(2L)
                .productName("IPhone")
                .price(100.0)
                .quantity(200)
                .build();
    }

    private Order getMockOrder() {
        return Order.builder()
                .orderStatus(OrderStatus.PLACED.value)
                .orderDate(Instant.now())
                .id(1L)
                .amount(100.0)
                .quantity(200)
                .productId(2L)
                .build();
    }
}