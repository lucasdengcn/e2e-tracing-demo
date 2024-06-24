package com.service.respository;

import com.google.common.collect.Lists;
import com.service.model.OrderRequest;
import com.service.model.Order;
import com.service.model.PayStartResponse;
import com.service.model.Status;
import io.opentelemetry.instrumentation.annotations.SpanAttribute;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.function.Consumer;

import static com.service.DaprClientHolder.*;

@Component
@Slf4j
public class OrderRepository {

    /**
     *
     * @param cart
     * @return
     */
    @WithSpan
    public Order createOrder(OrderRequest cart) throws Exception {
        log.info("Income order request: {}", cart);
        String id = UUID.randomUUID().toString();
        //
        String sqlText = "INSERT INTO orders(id, create_time, amount, status, cart_id)VALUES($1, $2, $3, $4, $5)";
        //
        Date createDate = new Date();
        List<Object> sqlArgs = Lists.newArrayListWithCapacity(5);
        sqlArgs.add(id);
        sqlArgs.add(createDate);
        sqlArgs.add(cart.getAmount());
        sqlArgs.add(Status.CREATED.getName());
        sqlArgs.add(cart.getCartId());
        String params = new JSONArray(sqlArgs).toString();
        //
        log.info("{}, {}", sqlText, params);
        //
        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put("sql", sqlText);
        metadata.put("params", params);
        // Invoke sql output binding using Dapr SDK
        daprClient.invokeBinding(sqlBindingName, "exec", null, metadata).block();
        //
        Order order = new Order(cart.getCartId(), id, createDate.getTime(), Status.CREATED, cart.getAmount());
        //
        return order;
    }

    /**
     *
     * @param id
     * @param status
     * @throws Exception
     */
    @WithSpan
    public void updateStatus(@SpanAttribute("order.id") String id, Status status) throws Exception {
        log.info("Update order request: {}, {}", id, status);
        //
        String sqlText = "UPDATE orders set status = $1, update_time = $2 where id = $3";
        //
        List<Object> sqlArgs = Lists.newArrayListWithCapacity(3);
        sqlArgs.add(status.getName());
        sqlArgs.add(new Date());
        sqlArgs.add(id);
        String params = new JSONArray(sqlArgs).toString();
        //
        log.info("{}, {}", sqlText, params);
        //
        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put("sql", sqlText);
        metadata.put("params", params);
        // Invoke sql output binding using Dapr SDK
        Mono<byte[]> binding = daprClient.invokeBinding(sqlBindingName, "exec", null, metadata);
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

    /**
     *
     * @throws Exception
     */
    @WithSpan
    public void updatePaymentStatus(Order order, PayStartResponse payStartResponse) throws Exception {
        log.info("updatePaymentStatus: {}, {}", order, payStartResponse);
        //
        String sqlText = "UPDATE orders set status = $1, update_time = $2, payment_txn_id = $3, payment_url = $4 where id = $5";
        //
        List<Object> sqlArgs = Lists.newArrayListWithCapacity(3);
        sqlArgs.add(order.getStatus().getName());
        sqlArgs.add(new Date());
        sqlArgs.add(payStartResponse.getTransactionId());
        sqlArgs.add(payStartResponse.getPageUrl());
        sqlArgs.add(order.getOrderId());
        String params = new JSONArray(sqlArgs).toString();
        //
        log.info("{}, {}", sqlText, params);
        //
        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put("sql", sqlText);
        metadata.put("params", params);
        // Invoke sql output binding using Dapr SDK
        Mono<byte[]> binding = daprClient.invokeBinding(sqlBindingName, "exec", null, metadata);
        binding.subscribe(new Consumer<byte[]>() {
            @Override
            public void accept(byte[] bytes) {
                log.info("updatePaymentStatus success. {}", order.getOrderId());
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) {
                log.info("updatePaymentStatus error. {}", order.getOrderId(), throwable);
            }
        }, new Runnable() {
            @Override
            public void run() {
                log.info("updatePaymentStatus completed. {}", order.getOrderId());
            }
        });
        //
    }


}
