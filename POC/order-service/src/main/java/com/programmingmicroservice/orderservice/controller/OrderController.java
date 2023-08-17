package com.programmingmicroservice.orderservice.controller;

import com.programmingmicroservice.orderservice.dtos.OrderRequest;
import com.programmingmicroservice.orderservice.dtos.OrderResponse;
import com.programmingmicroservice.orderservice.service.OrderService;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import io.opentelemetry.api.trace.Tracer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('client_admin')")
//    @Retry(name="inventory")
//    @Bulkhead(name = "inventory", type = Bulkhead.Type.THREADPOOL)
    @CircuitBreaker(name = "inventory", fallbackMethod = "fallBack")
//    @TimeLimiter(name="inventory",fallbackMethod="fallBackTimeout") //for timeouts we need to return completable future
    @ResponseStatus(HttpStatus.CREATED)
    public String placeOrder(@RequestBody OrderRequest orderRequest, HttpServletRequest request){
        String authorization = request.getHeader("Authorization");
        return orderService.placeOrder(orderRequest,authorization);
    }

    public String fallBack(OrderRequest orderRequest,HttpServletRequest request, RuntimeException runtimeException){
        return "Oops something went wrong!!!";
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<OrderResponse> getAllOrders(){
        return orderService.getAllOrders();
    }
}
