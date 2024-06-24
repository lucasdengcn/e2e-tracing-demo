package com.service.controller;

import com.service.model.Order;
import com.service.model.PayStartResponse;
import com.service.model.Status;
import com.service.respository.OrderRepository;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderProcessingService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private PaymentClient paymentClient;

    /**
     * In order to make @Async annotation work properly we have to follow 2 major rules
     *
     * Method annotated with @Async should be public
     * Method annotated with @Async should be called from different class, i.e calling async method within the same class doesn’t work
     *
     * @param order
     * @throws Exception
     */

    @Async("taskExecutorDefault")
    @WithSpan
    public void processOrder(Order order) throws Exception {
        //
        log.info("Process order request: {}", order);
        order.setStatus(Status.ACCEPTED);
        if (order.getAmount() > 1000){
            order.setStatus(Status.REJECTED);
        }
        //
        PayStartResponse payStartResponse = null;
        orderRepository.updateStatus(order.getOrderId(), order.getStatus());
        if (order.getStatus().equals(Status.ACCEPTED)){
            // start payment
            payStartResponse = paymentClient.startPaymentRequest(order.getOrderId(), order);
            order.setStatus(Status.PENDING_PAYMENT);
            orderRepository.updatePaymentStatus(order, payStartResponse);
        }
        //
        messageProducer.sendOrderProcessed(order, payStartResponse);
    }

}
