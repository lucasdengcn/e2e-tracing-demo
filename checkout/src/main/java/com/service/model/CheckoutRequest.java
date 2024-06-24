package com.service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class CheckoutRequest {
    private String cartId;
    private Integer amount;
    private Status status;
}
