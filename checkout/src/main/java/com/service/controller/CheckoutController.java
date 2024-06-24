package com.service.controller;


import com.service.model.*;
import com.service.processing.DaprClientHolder;
import com.service.respository.CartRepository;
import io.dapr.client.domain.HttpExtension;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Consumer;


@RestController
@Slf4j
public class CheckoutController {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private MessageProducer messageProducer;


    @PostMapping(path = "/checkout", consumes = MediaType.ALL_VALUE)
    public CheckoutResponse checkout(@RequestBody Cart body) throws Exception {
        log.info("Income checkout request: {}", body);
        //
        CheckoutRequest reqBody = cartRepository.create(body);
        body.setCartId(reqBody.getCartId());
        // OrderResult result = callOrderingAPIByHttpClient(reqBody);
        OrderResult result = callOrderingAPIBySDK(body);
        //
        cartRepository.updateStatus(reqBody.getCartId(), Status.SUBMITTED);
        result.setStatus(Status.SUBMITTED.getName());
        //
        //
        return new CheckoutResponse(body, result);
    }


    @PostMapping(path = "/preview", consumes = MediaType.ALL_VALUE)
    public CheckoutResponse preview(@RequestBody Cart body) throws Exception {
        log.info("Income checkout request: {}", body);
        //
        throw new RuntimeException("Request Error");
    }


    @PostMapping(path = "/checkout/event", consumes = MediaType.ALL_VALUE)
    public CheckoutResponse checkoutEvent(@RequestBody Cart body) throws Exception {
        log.info("Income checkout request: {}", body);
        //
        CheckoutRequest reqBody = cartRepository.create(body);
        body.setCartId(reqBody.getCartId());
        //
        messageProducer.sendCartCreated(body);
        //
        OrderResult result = buildOrderResult(body);
        //
        cartRepository.updateStatus(reqBody.getCartId(), Status.SUBMITTED);
        result.setStatus(Status.SUBMITTED.getName());
        //
        //
        return new CheckoutResponse(body, result);
    }

    private OrderResult callOrderingAPIByHttpClient(CheckoutRequest reqBody)
            throws Exception {
        //
        // curl http://localhost:3602/v1.0/invoke/checkout/method/checkout/100
        String dapr_url = "http://localhost:" + DaprClientHolder.DAPR_HTTP_PORT + "/orders";
        String body = new JSONObject(reqBody).toString();
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(URI.create(dapr_url))
                .header("Content-Type", "application/json")
                .header("dapr-app-id", "order-processor2")
                .build();

        log.info("Invoke Order Request: {}", reqBody);
        HttpResponse<String> response = DaprClientHolder.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("Receive Order Response: {}", response.body());
        //
        return buildOrderResult(response.body());
    }

    @NotNull
    private OrderResult buildOrderResult(String response) {
        JSONObject jsonObject = new JSONObject(response);
        return new OrderResult(jsonObject.getString("traceId"),
                jsonObject.getString("orderId"),
                jsonObject.getLong("dateTime"),
                jsonObject.getString("status"));
    }

    private OrderResult buildOrderResult(Cart reqBody) {
        return new OrderResult(
                reqBody.getCartId(),
                null,
                null,
                null);
    }

    @WithSpan
    private OrderResult callOrderingAPIBySDK(Cart reqBody) throws Exception {
        //Using Dapr SDK to invoke a method
        String appId = "order-processor2";
        var result = DaprClientHolder.daprClient.invokeMethod(
                appId,
                "orders",
                reqBody,
                HttpExtension.POST,
                OrderResult.class
        );
        log.info("Invoke Order Request: {}", reqBody);
        //
        result.subscribe(new Consumer<OrderResult>() {
            @Override
            public void accept(OrderResult response) {
                log.info("Receive Order Response: {}", response);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) {
                log.error("Invoke Error", throwable);
            }
        }, new Runnable() {
            @Override
            public void run() {
                log.info("Completed Order Request");
            }
        });
        //
        return buildOrderResult(reqBody);
    }

}
