package com.service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@ToString
public class OrderProcessedMessage {
    private String messageName;
    private OrderResult detail;
    private PayStartResponse payStart;
}
