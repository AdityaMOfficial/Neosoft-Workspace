package com.programmingmicro.productservice.repository;

import com.programmingmicro.productservice.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product,String> {
}
