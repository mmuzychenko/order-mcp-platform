package com.example.orders.mcp.tools;

import com.example.orders.contracts.order.SearchOrdersResponse;
import com.example.orders.mcp.client.OrderApiClient;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * MCP tool surface. Thin mapping only: syntax validation + forwarding trusted context.
 * No userId/tenantId parameters - identity comes from the authenticated MCP session.
 */
@Component
public class OrderTools {

    private final OrderApiClient api;

    public OrderTools(OrderApiClient api) {
        this.api = api;
    }

    @Tool(name = "search_orders",
          description = "READ-ONLY. Returns the caller's most recent orders, newest first. No side effects.")
    public SearchOrdersResponse searchOrders(
            @ToolParam(description = "Number of orders to return, 1..100") int limit) {
        if (limit < 1 || limit > 100) {
            throw new IllegalArgumentException("limit must be between 1 and 100");
        }
        return api.searchOrders(limit);
    }

    // TODO(Milestone 2): create_order - description must say SIDE-EFFECTING and require user confirmation
    // TODO(Milestone 2): get_order / get_operation_status
}
