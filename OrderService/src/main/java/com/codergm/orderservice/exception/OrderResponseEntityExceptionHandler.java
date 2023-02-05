package com.codergm.orderservice.exception;

import com.codergm.orderservice.model.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.ResponseEntity.status;

@ControllerAdvice
public class OrderResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(OrderException.class)
    public ResponseEntity<ErrorMessage> orderExceptionHandler(OrderException orderException) {
        return status(HttpStatus.NOT_FOUND).body(ErrorMessage
                .builder()
                .errorMessage(orderException.getMessage())
                .errorCode(orderException.getErrorCode())
                .build());
    }

    @ExceptionHandler(ProductServiceCustomException.class)
    public ResponseEntity<ErrorMessage> productExceptionHandler(ProductServiceCustomException exception){
        return status(HttpStatus.NOT_FOUND)
                .body(ErrorMessage.builder()
                        .errorCode(exception.getErrorCode())
                        .errorMessage(exception.getMessage())
                        .build());
    }

}
