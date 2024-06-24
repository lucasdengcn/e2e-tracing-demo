package com.service.respository;

import com.google.common.collect.Lists;
import com.service.model.Cart;
import com.service.model.CheckoutRequest;
import com.service.model.Status;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.instrumentation.annotations.SpanAttribute;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import com.service.processing.DaprClientHolder;

import java.util.*;
import java.util.function.Consumer;

@Component
@Slf4j
public class CartRepository {

    /**
     *
     * @param cart
     * @return
     */
    @WithSpan
    public CheckoutRequest create(Cart cart) throws Exception {
        log.info("Income checkout request: {}", cart);
        String id = UUID.randomUUID().toString();
        //
        String sqlText = "INSERT INTO carts(id, create_time, amount, status)VALUES($1, $2, $3, $4)";
        //
        List<Object> sqlArgs = Lists.newArrayListWithCapacity(4);
        sqlArgs.add(id);
        sqlArgs.add(new Date());
        sqlArgs.add(cart.getAmount());
        sqlArgs.add(Status.CREATED.getName());
        String params = new JSONArray(sqlArgs).toString();
        //
        log.info("{}, {}", sqlText, params);
        //
        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put("sql", sqlText);
        metadata.put("params", params);
        // Invoke sql output binding using Dapr SDK
        DaprClientHolder.daprClient.invokeBinding(DaprClientHolder.sqlBindingName, "exec", null, metadata).block();
        //
        CheckoutRequest reqBody = new CheckoutRequest(id, cart.getAmount(), Status.CREATED);
        // save into redis
        DaprClientHolder.daprClient.saveState(DaprClientHolder.CACHE_STATE_NAME, "demo_checkout:" + id, reqBody);
        //
        Span.current().setAttribute("app.cart.id", id);
        //
        return reqBody;
    }

    /**
     *
     * @param id
     * @param status
     * @throws Exception
     */
    @WithSpan
    public void updateStatus(@SpanAttribute("app.cart.id") String id, Status status) throws Exception {
        log.info("Update checkout request: {}, {}", id, status);
        //
        String sqlText = "UPDATE carts set status = $1, update_time = $2 where id = $3";
        //
        List<Object> sqlArgs = Lists.newArrayListWithCapacity(3);
        sqlArgs.add(status.getName());
        sqlArgs.add(new Date());
        sqlArgs.add(id);
        String params = new JSONArray(sqlArgs).toString();
        log.info("{}, {}", sqlText, params);
        //
        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put("sql", sqlText);
        metadata.put("params", params);
        // Invoke sql output binding using Dapr SDK
        Mono<byte[]> binding = DaprClientHolder.daprClient.invokeBinding(DaprClientHolder.sqlBindingName, "exec", null, metadata);
        binding.subscribe(new Consumer<byte[]>() {
            @Override
            public void accept(byte[] bytes) {
                log.info("updateStatus success. {}, {}", id, status);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) {
                log.info("updateStatus error. {}, {}", id, status, throwable);
            }
        }, new Runnable() {
            @Override
            public void run() {
                log.info("updateStatus completed. {}, {}", id, status);
            }
        });
        //
    }

}
