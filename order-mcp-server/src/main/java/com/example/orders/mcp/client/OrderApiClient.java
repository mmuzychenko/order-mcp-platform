package com.example.orders.mcp.client;

import com.example.orders.contracts.order.SearchOrdersResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/** HTTP client to the Java Order API. Forwards correlation ID and an audience-scoped token. */
@Component
public class OrderApiClient {

    private final RestClient restClient;

    public OrderApiClient(RestClient orderApiRestClient) {
        this.restClient = orderApiRestClient;
    }

    public SearchOrdersResponse searchOrders(int limit) {
        return restClient.get()
                .uri(uri -> uri.path("/api/v1/orders").queryParam("limit", limit).build())
                .retrieve()
                .body(SearchOrdersResponse.class);
        // TODO: map ErrorEnvelope responses to MCP tool errors (see error package)
    }
}
