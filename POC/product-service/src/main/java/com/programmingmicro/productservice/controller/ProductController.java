package com.programmingmicro.productservice.controller;

import com.programmingmicro.productservice.dtos.ProductRequest;
import com.programmingmicro.productservice.dtos.ProductResponse;
import com.programmingmicro.productservice.model.Product;
import com.programmingmicro.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct(@RequestBody ProductRequest productRequest){
        productService.createProduct(productRequest);
    }

    @GetMapping
    @PreAuthorize("hasRole('client_user')")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getAllProducts(){
        return productService.getAllProducts();
    }


    @GetMapping("/admin")
    @PreAuthorize("hasRole('client_admin')")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getAllProducts2(){
        return productService.getAllProducts();
    }

}
