package com.vendor.payment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@AllArgsConstructor
@ToString
public class OrderPayResponse {
    private String transactionId;
    private OrderPayRequest orderPayRequest;
    private String pageUrl;
}
