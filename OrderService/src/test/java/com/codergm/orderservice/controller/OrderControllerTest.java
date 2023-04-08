package com.codergm.orderservice.controller;

import com.codergm.orderservice.OrderServiceConfig;
import com.codergm.orderservice.entity.Order;
import com.codergm.orderservice.model.OrderRequest;
import com.codergm.orderservice.model.OrderStatus;
import com.codergm.orderservice.model.PaymentMode;
import com.codergm.orderservice.repository.OrderRepository;
import com.codergm.orderservice.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.util.StreamUtils.copyToString;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;


@SpringBootTest({"server.port=0"})
@EnableConfigurationProperties
@AutoConfigureMockMvc
@ContextConfiguration(classes = {OrderServiceConfig.class})
public class OrderControllerTest {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderService orderSevice;
    @Autowired
    private MockMvc mockMvc;

    @RegisterExtension

    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().port(8080))
            .build();

    private ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @BeforeEach
    void setup() throws IOException {
        getProductDetailsResponse();
        doPayment();
        getPaymentDetails();
        reduceQuantity();
    }


    @Test
    @DisplayName("Place Order - Successful")
    public void test_When_PlaceOrder_DoPayment_Success() throws Exception {
        OrderRequest orderRequest = getMockOrderRequest();
        // First place Order
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/order/place-order")
                        .with(jwt().authorities(new SimpleGrantedAuthority("Customer")))
                        .contentType(APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();
        String orderId = mvcResult.getResponse().getContentAsString();

        // Get Order by Order Id from Db and check
        Optional<Order> order = orderRepository.findById(Long.valueOf(orderId));

        // Check output
        assertTrue(order.isPresent());

        Order o = order.get();

        assertEquals(Long.valueOf(orderId), o.getId());
        assertEquals(OrderStatus.PLACED.value, o.getOrderStatus());
        assertEquals(orderRequest.getTotalAmount(), o.getAmount());
        assertEquals(orderRequest.getQuantity(), o.getQuantity());


    }

    @Test
    @DisplayName("Place Order - Wrong Access")
    void test_WhenPlaceOrderWithWrongAccess_thenThrow403() throws Exception {
        OrderRequest orderRequest = getMockOrderRequest();
        mockMvc.perform(MockMvcRequestBuilders.post("/order/place-order")
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(orderRequest))
                .with(jwt().authorities(new SimpleGrantedAuthority("Admin"), new SimpleGrantedAuthority("Customer2")))
        ).andExpect(MockMvcResultMatchers.status().isForbidden()).andReturn();
    }

    @Test
    @DisplayName("Get Order - Success")
    void test_WhenGetOrder_Success() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("")).andReturn();
    }

    private OrderRequest getMockOrderRequest() {
        return OrderRequest.builder()
                .productId(1L)
                .paymentMode(PaymentMode.CASH)
                .totalAmount(200.0)
                .quantity(10)
                .build();
    }


    private void reduceQuantity() {
        StubMapping stubMapping = wireMockServer.stubFor(put(urlMatching("/product/reduce-quantity/.*"))

                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", APPLICATION_JSON_VALUE)));
        System.out.println("reduceQuantity: " + stubMapping.getResponse().getBody());
    }

    private void getPaymentDetails() throws IOException {

        String responseBody = copyToString(
                OrderControllerTest.class
                        .getClassLoader()
                        .getResourceAsStream("mock/GetPayment.json"),
                Charset.defaultCharset());
        StubMapping result = wireMockServer.stubFor(get(urlMatching("/payment/.*"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                        .withBody(responseBody)));
    }

    private void doPayment() {
        wireMockServer.stubFor(post(urlEqualTo("/payment"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", APPLICATION_JSON_VALUE)));
    }

    private void getProductDetailsResponse() throws IOException {
        // Get /product/1
        String responseBody = copyToString(
                OrderControllerTest.class
                        .getClassLoader()
                        .getResourceAsStream("mock/GetProduct.json"),
                Charset.defaultCharset());
        wireMockServer.stubFor(get("/product/1")
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                        .withBody(responseBody)));
    }
}