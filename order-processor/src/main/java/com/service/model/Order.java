package com.service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@AllArgsConstructor
@ToString
public class Order {
    private String cartId;
    private String orderId;
    private Long dateTime;
    private Status status;
    private Integer amount;
}
