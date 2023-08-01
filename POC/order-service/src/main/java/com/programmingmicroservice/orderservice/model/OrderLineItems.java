package com.programmingmicroservice.orderservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "t_order_line_items")
public class OrderLineItems {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String skuCode;

    private BigDecimal price;

    private Integer quantity;


}
