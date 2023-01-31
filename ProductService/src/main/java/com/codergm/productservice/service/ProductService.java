package com.codergm.productservice.service;

import com.codergm.productservice.model.ProductRequest;
import com.codergm.productservice.model.ProductResponse;

public interface ProductService {
    long addProduct(com.codergm.productservice.model.ProductRequest productRequest);

    ProductResponse findProductById(Long id);
}
