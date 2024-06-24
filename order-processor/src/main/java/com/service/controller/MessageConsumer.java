package com.service.controller;

import com.service.model.Order;
import com.service.model.OrderRequest;
import com.service.respository.OrderRepository;
import io.dapr.Topic;
import io.dapr.client.domain.CloudEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Component
@Slf4j
public class MessageConsumer {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderProcessingService orderProcessingService;


    @Topic(name = "checkout", pubsubName = "kafka-pubsub")
    @PostMapping(path = "/orders/messages", consumes = MediaType.ALL_VALUE)
    public void processCheckoutMessage(@RequestBody(required = false) CloudEvent<OrderRequest> cloudEvent) throws Exception {
        OrderRequest cart = cloudEvent.getData();
        log.info("Subscribed event: {}, data: {}", cloudEvent, cart);
        Order order = orderRepository.createOrder(cart);
        orderProcessingService.processOrder(order);
    }


}
