package com.codergm.orderservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "order_details")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "order_seq")
    private Long id;
    private Long productId;
    private Integer quantity;
    private Instant orderDate;
    private String orderStatus;
    private Double amount;
}
