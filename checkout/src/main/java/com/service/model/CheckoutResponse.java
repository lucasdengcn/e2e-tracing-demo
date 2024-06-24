package com.service.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CheckoutResponse {

    private Cart request;
    private OrderResult result;
}
