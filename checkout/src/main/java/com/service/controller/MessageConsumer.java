package com.service.controller;

import com.service.model.OrderProcessedMessage;
import io.dapr.Topic;
import io.dapr.client.domain.CloudEvent;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.instrumentation.annotations.SpanAttribute;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class MessageConsumer {

    @Topic(name = "orders", pubsubName = "kafka-pubsub")
    @PostMapping(path = "/checkout/messages", consumes = MediaType.ALL_VALUE)
    public void receiveMessage(@RequestBody(required = false) CloudEvent<OrderProcessedMessage> cloudEvent) {
        consumeOrderMessage(cloudEvent.getData().getDetail().getOrderId(), cloudEvent);
    }

    @WithSpan(kind = SpanKind.CONSUMER)
    private void consumeOrderMessage(@SpanAttribute(value = "order.id") String orderId,
                                     CloudEvent<OrderProcessedMessage> cloudEvent){
        OrderProcessedMessage order = cloudEvent.getData();
        //
        log.info("Subscribed event: {}, data: {}", cloudEvent, order);
    }

}
