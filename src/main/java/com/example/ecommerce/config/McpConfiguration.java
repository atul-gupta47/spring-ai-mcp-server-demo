package com.example.ecommerce.config;

import org.springframework.ai.mcp.server.McpServerConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(McpServerConfiguration.class)
public class McpConfiguration {
    // MCP server configuration is handled by Spring AI
}
