package com.example.orders.mcp.config;

import com.example.orders.mcp.tools.OrderTools;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Exposes @Tool methods via MCP tools/list and tools/call. */
@Configuration
public class ToolRegistrationConfig {

    @Bean
    ToolCallbackProvider orderToolCallbacks(OrderTools orderTools) {
        return MethodToolCallbackProvider.builder().toolObjects(orderTools).build();
    }
}
