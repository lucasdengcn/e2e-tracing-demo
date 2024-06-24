package com.service.controller;

import com.service.model.Cart;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import com.service.processing.DaprClientHolder;

import java.util.function.Consumer;

@RestController
@Slf4j
public class MessageProducer {


    @WithSpan(kind = SpanKind.PRODUCER)
    public void sendCartCreated(Cart body){
        //
        Mono<Void> publishEvent = DaprClientHolder.daprClient.publishEvent(
                DaprClientHolder.PUBSUB_NAME,
                DaprClientHolder.TOPIC_NAME, body);
        publishEvent.subscribe(new Consumer<Void>() {
            @Override
            public void accept(Void unused) {
                log.info("Publish event success: {}", body);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) {
                log.info("Publish event error", throwable);
            }
        }, new Runnable() {
            @Override
            public void run() {
                log.info("Publish event completed: {}", body);
            }
        });
        //
        log.info("Publishing event ongoing: {}", body);
    }

}
