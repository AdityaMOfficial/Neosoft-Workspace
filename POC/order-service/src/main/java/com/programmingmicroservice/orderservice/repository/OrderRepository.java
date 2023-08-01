package com.programmingmicroservice.orderservice.repository;

import com.programmingmicroservice.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,Long> {
}
