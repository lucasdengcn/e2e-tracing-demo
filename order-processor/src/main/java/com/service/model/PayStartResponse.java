package com.service.model;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PayStartResponse {
    private String transactionId;
    private String pageUrl;
}
