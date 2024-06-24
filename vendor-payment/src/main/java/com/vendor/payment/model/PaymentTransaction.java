package com.vendor.payment.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PaymentTransaction {
    private String id;
    private String orderId;
    private String status;
    private boolean synced;
    private Integer amount;
    private String traceId;

    public PaymentTransaction(String id, String orderId, String traceId, Integer amount) {
        this.id = id;
        this.orderId = orderId;
        this.traceId = traceId;
        this.amount = amount;
    }
}
