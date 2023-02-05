package com.codergm.orderservice.external.decoder;

import com.codergm.orderservice.exception.ProductServiceCustomException;
import com.codergm.orderservice.external.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class CustomErrorDecoder implements ErrorDecoder {
    @SneakyThrows
    @Override
    public Exception decode(String s, Response response) {
        ObjectMapper objectMapper = new ObjectMapper();
        log.info("::{}",response.request().url());
        log.info("::{}",response.request().headers());
        ErrorResponse errorResponse = objectMapper.readValue(response.request().body(), ErrorResponse.class);
        return new ProductServiceCustomException(errorResponse.getErrorMessage(), errorResponse.getErrorCode());
    }
}
