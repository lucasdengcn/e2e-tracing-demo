package com.vendor.payment.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.PostExchange;

import java.util.Map;

/**
 * https://www.danvega.dev/blog/rest-client-first-look
 */
public interface OrderClient {

    @PostExchange(url = "/callback/payment/{orderId}", contentType="application/json")
    void updatePaymentStatus(@PathVariable String orderId, @RequestBody boolean success, @RequestHeader("traceparent") String traceParent);

    @PostExchange(url = "/callback/payment/{orderId}", contentType="application/json")
    void updatePaymentStatus(@PathVariable String orderId, @RequestBody boolean success);

}
