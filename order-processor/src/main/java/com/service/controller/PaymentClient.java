package com.service.controller;

import com.service.DaprClientHolder;
import com.service.model.Order;
import com.service.model.PayStartResponse;
import io.dapr.client.domain.HttpExtension;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.instrumentation.annotations.SpanAttribute;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * https://docs.dapr.io/reference/resource-specs/httpendpoints-schema/
 * https://docs.dapr.io/developing-applications/building-blocks/service-invocation/howto-invoke-non-dapr-endpoints/
 */
@Component
@Slf4j
public class PaymentClient {

    private String httpEndpointName = "httpPayment";

    /**
     *
     * @param order
     * @return
     */
    @WithSpan(kind = SpanKind.CLIENT)
    public PayStartResponse startPaymentRequest(@SpanAttribute("order.id") String orderId, Order order){
        var result = DaprClientHolder.daprClient.invokeMethod(
                httpEndpointName,
                "start",
                order,
                HttpExtension.POST,
                PayStartResponse.class
        );
        log.info("Invoke Payment Request: {}, {}", orderId, order);
        //
        PayStartResponse payStartResponse = result.block();
        //
        return payStartResponse;
    }

}
