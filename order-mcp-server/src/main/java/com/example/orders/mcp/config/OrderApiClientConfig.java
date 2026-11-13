package com.example.orders.mcp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class OrderApiClientConfig {

    @Bean
    RestClient orderApiRestClient(@Value("${order-api.base-url}") String baseUrl,
                                  @Value("${order-api.timeout:5s}") Duration timeout) {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(timeout);
        factory.setReadTimeout(timeout);
        // TODO(D3): add an interceptor that performs token exchange (audience=order-api)
        //           and forwards X-Correlation-Id. Do NOT pass through unrelated bearer tokens.
        return RestClient.builder().baseUrl(baseUrl).requestFactory(factory).build();
    }
}
