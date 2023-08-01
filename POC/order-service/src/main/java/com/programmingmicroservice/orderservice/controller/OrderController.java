package com.programmingmicroservice.orderservice.controller;

import com.programmingmicroservice.orderservice.dtos.OrderRequest;
import com.programmingmicroservice.orderservice.dtos.OrderResponse;
import com.programmingmicroservice.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String placeOrder(@RequestBody OrderRequest orderRequest){
        orderService.placeOrder(orderRequest);
        return "Order placed successfully";
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<OrderResponse> getAllOrders(){
        return orderService.getAllOrders();
    }
}
