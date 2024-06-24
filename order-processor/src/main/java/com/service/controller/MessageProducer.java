package com.service.controller;

import com.service.model.Order;
import com.service.model.OrderProcessedMessage;
import com.service.model.PayStartResponse;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

import static com.service.DaprClientHolder.*;

@Component
@Slf4j
public class MessageProducer {

    @WithSpan(kind = SpanKind.PRODUCER)
    public void sendOrderProcessed(Order order, PayStartResponse payStartResponse){
        //
        OrderProcessedMessage orderProcessed = new OrderProcessedMessage("OrderProcessed", order, payStartResponse);
        Mono<Void> publishEvent = daprClient.publishEvent(
                PUBSUB_NAME,
                TOPIC_NAME, orderProcessed);
        publishEvent.subscribe(new Consumer<Void>() {
            @Override
            public void accept(Void unused) {
                log.info("Publish event success: {}", orderProcessed);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) {
                log.info("Publish event error", throwable);
            }
        }, new Runnable() {
            @Override
            public void run() {
                log.info("Publish event completed: {}", orderProcessed);
            }
        });
        //
        log.info("Publishing event ongoing: {}", orderProcessed);
    }

}
