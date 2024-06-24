package com.vendor.payment.controller;

import com.vendor.payment.TraceHolder;
import com.vendor.payment.model.OrderPayRequest;
import com.vendor.payment.model.OrderPayResponse;
import com.vendor.payment.model.OrderPayResultResponse;
import com.vendor.payment.model.PaymentTransaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@Slf4j
public class OnlinePayController {

    private final ConcurrentHashMap<String, PaymentTransaction> payRequestConcurrentHashMap = new ConcurrentHashMap<>();
    //
    @Autowired
    private OrderClient orderClient;


    @PostMapping(path = "/start", consumes = MediaType.ALL_VALUE)
    public OrderPayResponse payStart(@RequestBody OrderPayRequest payRequest) throws Exception {
        log.info("Income payStart Request: {}", payRequest);
        //
        //
        String uuid = UUID.randomUUID().toString();
        String url = "http://localhost:19102/confirm/" + uuid;
        //
        PaymentTransaction paymentTransaction = new PaymentTransaction(uuid, payRequest.getOrderId(), TraceHolder.getCurrentTraceId(), payRequest.getAmount());
        payRequestConcurrentHashMap.put(uuid, paymentTransaction);
        //
        return new OrderPayResponse(uuid, payRequest, url);
    }

    @PostMapping(path = "/confirm/{id}", consumes = MediaType.ALL_VALUE)
    public OrderPayResultResponse payConfirm(@PathVariable String id) throws Exception {
        log.info("Income payConfirm Request: {}", id);
        // call order api
        PaymentTransaction orderPayRequest = payRequestConcurrentHashMap.get(id);
        //
        orderClient.updatePaymentStatus(orderPayRequest.getOrderId(), true, TraceHolder.buildTraceParent(orderPayRequest.getTraceId()));
        //
        return new OrderPayResultResponse(id, null, true);
    }

}
