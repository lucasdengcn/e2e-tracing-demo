package com.service.controller;

import com.service.model.Order;
import com.service.model.OrderRequest;
import com.service.model.PayStartResponse;
import com.service.model.Status;
import com.service.respository.OrderRepository;
import io.dapr.Topic;
import io.dapr.client.domain.CloudEvent;
import io.opentelemetry.instrumentation.annotations.AddingSpanAttributes;
import io.opentelemetry.instrumentation.annotations.SpanAttribute;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RestController
@Slf4j
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private PaymentClient paymentClient;

    @Autowired
    private OrderProcessingService orderProcessingService;


    @PostMapping(path = "/orders", consumes = MediaType.ALL_VALUE)
    public Order createOrder(@RequestBody OrderRequest cart) throws Exception {
        log.info("Income Order Request: {}", cart);
        //
        Order order = orderRepository.createOrder(cart);
        //
        orderProcessingService.processOrder(order);
        //
        return order;
    }

    @PostMapping(path = "/callback/payment/{orderId}", consumes = MediaType.ALL_VALUE)
    @AddingSpanAttributes
    public void paymentCallback(@SpanAttribute("order.id") @PathVariable String orderId, @RequestBody String body) throws Exception {
        log.info("Income paymentCallback Request: {}, {}", orderId, body);
        //
        orderRepository.updateStatus(orderId, Status.PAYMENT_SUCCESS);
    }

    @Topic(name = "checkout", pubsubName = "kafka-pubsub")
    @PostMapping(path = "/orders/messages", consumes = MediaType.ALL_VALUE)
    public void processCheckoutMessage(@RequestBody(required = false) CloudEvent<OrderRequest> cloudEvent) throws Exception {
        OrderRequest cart = cloudEvent.getData();
        log.info("Subscribed event: {}, data: {}", cloudEvent, cart);
        Order order = orderRepository.createOrder(cart);
        orderProcessingService.processOrder(order);
    }

}
