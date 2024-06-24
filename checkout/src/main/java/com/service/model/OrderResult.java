package com.service.model;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderResult {

    private String cartId;
    private String orderId;
    private Long dateTime;
    private String status;
}
