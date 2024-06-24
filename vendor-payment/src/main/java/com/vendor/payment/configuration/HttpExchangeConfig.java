package com.vendor.payment.configuration;

import com.vendor.payment.controller.OrderClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.io.IOException;
import java.util.List;
import java.util.function.BiConsumer;

@Slf4j
@Configuration
public class HttpExchangeConfig {

    private ClientHttpRequestInterceptor interceptor = new ClientHttpRequestInterceptor() {
        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
            // request.getHeaders().set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
            request.getHeaders().forEach(new BiConsumer<String, List<String>>() {
                @Override
                public void accept(String s, List<String> strings) {
                    log.info("{}, {}", s, strings);
                }
            });
            return execution.execute(request, body);
        }
    };

    @Bean
    public OrderClient orderClient(RestClient.Builder restClientBuilder, @Value("${client.order.url}") String url){
        RestClient restClient = restClientBuilder.baseUrl(url).requestInterceptor(interceptor).build();
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(OrderClient.class);
    }


}
