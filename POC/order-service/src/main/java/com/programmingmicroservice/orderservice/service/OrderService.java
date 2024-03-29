package com.programmingmicroservice.orderservice.service;

import com.programmingmicroservice.orderservice.dtos.InventoryResponse;
import com.programmingmicroservice.orderservice.dtos.OrderLineItemsDto;
import com.programmingmicroservice.orderservice.dtos.OrderRequest;
import com.programmingmicroservice.orderservice.dtos.OrderResponse;
import com.programmingmicroservice.orderservice.events.OrderPlacedEvent;
import com.programmingmicroservice.orderservice.model.Order;
import com.programmingmicroservice.orderservice.model.OrderLineItems;
import com.programmingmicroservice.orderservice.repository.OrderRepository;
//import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;

    private final WebClient.Builder webClientBuilder;

    private final KafkaTemplate<String,OrderPlacedEvent> kafkaTemplate;

    private final Tracer tracer;

    public String placeOrder(OrderRequest orderRequest, String authorization){


        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> orderLineItemsList = orderRequest.getOrderLineItemsDtoList().stream().map(
                orderLineItemsDto -> {
                    return OrderLineItems.builder()
                            .price(orderLineItemsDto.getPrice())
                            .skuCode(orderLineItemsDto.getSkuCode().replace(" ","_").toLowerCase(Locale.ROOT))
                            .quantity(orderLineItemsDto.getQuantity())
                            .build();
                }
        ).collect(Collectors.toList());
        order.setOrderLineItemsList(orderLineItemsList);
        //call inventory service and place order if order is in stock.

        List<String> skuCodes = order.getOrderLineItemsList().stream()
                .map(orderLineItems -> orderLineItems.getSkuCode()).collect(Collectors.toList());

        Span inventoryServiceLookup = tracer.spanBuilder("inventoryServiceLookup").startSpan();


        InventoryResponse[] inventoryResponses = null;
        try(Scope scope = inventoryServiceLookup.makeCurrent()){

            inventoryResponses  = webClientBuilder.build().get()
                    .uri("http://INVENTORY-SERVICE/api/inventory",
                            uriBuilder -> {
                                URI builtURI = uriBuilder.queryParam("skuCode", skuCodes)
                                        .build();
                                log.info("Web-client uri {}",builtURI.toString());
                                return builtURI;
                            })
                    .headers(httpHeaders -> httpHeaders.add("Authorization",authorization))
                    .retrieve()
                    .bodyToMono(InventoryResponse[].class)
                    .block();
        }finally{
            inventoryServiceLookup.end();
        }



        log.info("Inventory Response List {}",inventoryResponses);

        boolean allProductsInStock = Arrays.stream(inventoryResponses)
                .allMatch(InventoryResponse::getIsInStock);

        if(allProductsInStock) {
            orderRepository.save(order);
            kafkaTemplate.send("notificationTopic",new OrderPlacedEvent(order.getOrderNumber()));
            return "Order placed successfully";
        }
        else
            throw new IllegalArgumentException("Product is not in stock");
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



//    ExchangeFilterFunction urlModifyingFilter = (clientRequest, nextFilter) -> {
//        String token = null;
//        if(this.manager!=null) {
//            OAuth2AuthorizedClient authorize = this.manager.authorize(OAuth2AuthorizeRequest.withClientRegistrationId("my-client").principal("internal").build());
//
//            token = authorize.getAccessToken().getTokenValue();
//            if (token != null)
//                clientRequest.headers().add("Authorization", "Bearer " + token);
//        }
//        return nextFilter.exchange(clientRequest);
//    };
}
