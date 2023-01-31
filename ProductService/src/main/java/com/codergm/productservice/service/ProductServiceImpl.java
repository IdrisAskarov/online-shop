package com.codergm.productservice.service;

import com.codergm.productservice.entity.Product;
import com.codergm.productservice.model.ProductRequest;
import com.codergm.productservice.model.ProductResponse;
import com.codergm.productservice.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import static org.springframework.beans.BeanUtils.copyProperties;

@Service
@AllArgsConstructor
@Log4j2
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public long addProduct(ProductRequest productRequest) {
        log.info("Adding product...");
        Product product = Product
                .builder()
                .price(productRequest.getPrice())
                .productName(productRequest.getName())
                .quantity(productRequest.getQuantity())
                .build();
        productRepository.save(product);
        log.info("Product created");
        return product.getProductId();
    }

    @Override
    public ProductResponse findProductById(Long id) {
        log.info("Get the product for productId: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(""));
        ProductResponse response = new ProductResponse();
        copyProperties(product,response);
        return response;
    }
}
