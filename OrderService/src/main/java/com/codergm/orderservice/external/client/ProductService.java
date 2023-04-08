package com.codergm.orderservice.external.client;

import com.codergm.orderservice.exception.OrderException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@CircuitBreaker(name = "external",fallbackMethod = "fallback")
@FeignClient(name = "product-service/product")
public interface ProductService {

    @PutMapping("/reduce-quantity/{id}")
    ResponseEntity<Void> reduceQuantity(@PathVariable("id") Long productId,
                                               @RequestParam Integer quantity);

    default ResponseEntity<Void>  fallback(Long productId, Integer quantity, Exception e){
        e.printStackTrace();
        throw  new OrderException("Product Service is not available","INTERNAL_SERVER_ERROR");
    }

}
