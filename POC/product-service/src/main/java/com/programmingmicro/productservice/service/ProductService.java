package com.programmingmicro.productservice.service;

import com.programmingmicro.productservice.dtos.ProductRequest;
import com.programmingmicro.productservice.dtos.ProductResponse;
import com.programmingmicro.productservice.model.Product;
import com.programmingmicro.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public void createProduct(ProductRequest productRequest){
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .build();

        productRepository.save(product);
        log.info("Product {} is saved",product.getId());
    }

    public List<ProductResponse> getAllProducts() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        log.info("Email id :: {}",authentication.getPrincipal());
        List<Product> products = productRepository.findAll();
        List<ProductResponse> productResponseList = products.stream().map(product -> {
            return ProductResponse.builder()
                            .id(product.getId())
                            .description(product.getDescription())
                            .name(product.getName())
                            .price(product.getPrice())
                            .build();
        }).collect(Collectors.toList());
        return productResponseList;
    }
}
