package com.codergm.orderservice.exception;

import lombok.Data;

@Data
public class OrderException extends RuntimeException{
    private String errorCode;

    public OrderException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
