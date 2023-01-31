package com.codergm.productservice.model;

import lombok.Data;

@Data
public class ProductRequest {
    private String name;
    private Double price;
    private Integer quantity;
}
