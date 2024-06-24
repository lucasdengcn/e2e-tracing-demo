package com.service.processing;

import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;

import java.net.http.HttpClient;
import java.time.Duration;

public class DaprClientHolder {

    public static String TOPIC_NAME = "checkout";
    public static String PUBSUB_NAME = "kafka-pubsub";
    public static String CACHE_STATE_NAME = "cache_redis";

    public static String sqlBindingName = "pg_demo_cart";

    public final static HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public static final String DAPR_HTTP_PORT = System.getenv().getOrDefault("DAPR_HTTP_PORT", "3500");
    //
    public final static DaprClient daprClient = (new DaprClientBuilder()).build();

}