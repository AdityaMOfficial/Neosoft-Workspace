package com.programmingmicroservice.orderservice.service;

import com.programmingmicroservice.orderservice.dtos.OrderLineItemsDto;
import com.programmingmicroservice.orderservice.dtos.OrderRequest;
import com.programmingmicroservice.orderservice.dtos.OrderResponse;
import com.programmingmicroservice.orderservice.model.Order;
import com.programmingmicroservice.orderservice.model.OrderLineItems;
import com.programmingmicroservice.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;

    public void placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> orderLineItemsList = orderRequest.getOrderLineItemsDtoList().stream().map(
                orderLineItemsDto -> {
                    return OrderLineItems.builder()
                            .price(orderLineItemsDto.getPrice())
                            .skuCode(orderLineItemsDto.getSkuCode())
                            .quantity(orderLineItemsDto.getQuantity())
                            .build();
                }
        ).collect(Collectors.toList());
        order.setOrderLineItemsList(orderLineItemsList);
        orderRepository.save(order);
    }

    public List<OrderResponse> getAllOrders(){
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(order -> {
            return OrderResponse.builder()
                    .orderNumber(order.getOrderNumber())
                    .orderLineItemsDtoList(convert(order.getOrderLineItemsList()))
                    .build();
        }).collect(Collectors.toList());
    }

    private List<OrderLineItemsDto> convert(List<OrderLineItems> orderLineItemsList) {
        return orderLineItemsList.stream().map(
                orderLineItems -> {
                    return OrderLineItemsDto.builder()
                            .id(orderLineItems.getId())
                            .price(orderLineItems.getPrice())
                            .skuCode(orderLineItems.getSkuCode())
                            .quantity(orderLineItems.getQuantity())
                            .build();
                }
        ).collect(Collectors.toList());
    }
}
