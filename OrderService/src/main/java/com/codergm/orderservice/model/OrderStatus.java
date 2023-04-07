package com.codergm.orderservice.model;

public enum OrderStatus {
    CREATED("CREATED"), PLACED("PLACED"), PAYMENT_FAILED("PAYMENT_FAILED");
    OrderStatus(String status){
        value = status;
    }
    public  final String value;

}
