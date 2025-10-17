package com.example.ecommerce.mcp;

import com.example.ecommerce.dto.CustomerDto;
import com.example.ecommerce.dto.OrderDto;
import com.example.ecommerce.dto.ProductDto;
import com.example.ecommerce.service.CustomerService;
import com.example.ecommerce.service.OrderService;
import com.example.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.mcp.server.McpServer;
import org.springframework.ai.mcp.server.McpTool;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class EcommerceMcpServer {
    
    private final CustomerService customerService;
    private final ProductService productService;
    private final OrderService orderService;
    
    @McpTool(
        name = "create_customer",
        description = "Create a new customer with personal information and address"
    )
    public Map<String, Object> createCustomer(
            @McpTool.Parameter(description = "Customer's first name") String firstName,
            @McpTool.Parameter(description = "Customer's last name") String lastName,
            @McpTool.Parameter(description = "Customer's email address") String email,
            @McpTool.Parameter(description = "Customer's phone number") String phone,
            @McpTool.Parameter(description = "Street address") String street,
            @McpTool.Parameter(description = "City") String city,
            @McpTool.Parameter(description = "State") String state,
            @McpTool.Parameter(description = "ZIP code") String zipCode,
            @McpTool.Parameter(description = "Country") String country) {
        
        try {
            CustomerDto customerDto = new CustomerDto();
            customerDto.setFirstName(firstName);
            customerDto.setLastName(lastName);
            customerDto.setEmail(email);
            customerDto.setPhone(phone);
            
            CustomerDto.AddressDto address = new CustomerDto.AddressDto();
            address.setStreet(street);
            address.setCity(city);
            address.setState(state);
            address.setZipCode(zipCode);
            address.setCountry(country);
            customerDto.setAddress(address);
            
            CustomerDto createdCustomer = customerService.createCustomer(customerDto);
            
            return Map.of(
                "success", true,
                "message", "Customer created successfully",
                "customer", createdCustomer
            );
        } catch (Exception e) {
            log.error("Error creating customer", e);
            return Map.of(
                "success", false,
                "message", "Failed to create customer: " + e.getMessage()
            );
        }
    }
    
    @McpTool(
        name = "create_product",
        description = "Create a new product with details like name, price, category, and stock"
    )
    public Map<String, Object> createProduct(
            @McpTool.Parameter(description = "Product name") String name,
            @McpTool.Parameter(description = "Product description") String description,
            @McpTool.Parameter(description = "Product price") Double price,
            @McpTool.Parameter(description = "Product category") String category,
            @McpTool.Parameter(description = "Product SKU") String sku,
            @McpTool.Parameter(description = "Stock quantity") Integer stockQuantity) {
        
        try {
            ProductDto productDto = new ProductDto();
            productDto.setName(name);
            productDto.setDescription(description);
            productDto.setPrice(java.math.BigDecimal.valueOf(price));
            productDto.setCategory(category);
            productDto.setSku(sku);
            productDto.setStockQuantity(stockQuantity);
            
            ProductDto createdProduct = productService.createProduct(productDto);
            
            return Map.of(
                "success", true,
                "message", "Product created successfully",
                "product", createdProduct
            );
        } catch (Exception e) {
            log.error("Error creating product", e);
            return Map.of(
                "success", false,
                "message", "Failed to create product: " + e.getMessage()
            );
        }
    }
    
    @McpTool(
        name = "create_order",
        description = "Create a new order for a customer with multiple products"
    )
    public Map<String, Object> createOrder(
            @McpTool.Parameter(description = "Customer ID") Long customerId,
            @McpTool.Parameter(description = "List of product IDs and quantities as JSON string") String orderItemsJson) {
        
        try {
            // Parse order items from JSON string
            // For simplicity, we'll expect format: "productId1:quantity1,productId2:quantity2"
            OrderDto orderDto = new OrderDto();
            orderDto.setCustomerId(customerId);
            
            List<OrderDto.OrderItemDto> orderItems = parseOrderItems(orderItemsJson);
            orderDto.setOrderItems(orderItems);
            
            OrderDto createdOrder = orderService.createOrder(orderDto);
            
            return Map.of(
                "success", true,
                "message", "Order created successfully",
                "order", createdOrder
            );
        } catch (Exception e) {
            log.error("Error creating order", e);
            return Map.of(
                "success", false,
                "message", "Failed to create order: " + e.getMessage()
            );
        }
    }
    
    @McpTool(
        name = "get_customer",
        description = "Get customer information by ID or email"
    )
    public Map<String, Object> getCustomer(
            @McpTool.Parameter(description = "Customer ID (optional if email provided)") Long customerId,
            @McpTool.Parameter(description = "Customer email (optional if ID provided)") String email) {
        
        try {
            CustomerDto customer;
            if (customerId != null) {
                customer = customerService.getCustomerById(customerId);
            } else if (email != null) {
                customer = customerService.getCustomerByEmail(email);
            } else {
                return Map.of(
                    "success", false,
                    "message", "Either customer ID or email must be provided"
                );
            }
            
            return Map.of(
                "success", true,
                "customer", customer
            );
        } catch (Exception e) {
            log.error("Error getting customer", e);
            return Map.of(
                "success", false,
                "message", "Failed to get customer: " + e.getMessage()
            );
        }
    }
    
    @McpTool(
        name = "get_product",
        description = "Get product information by ID, SKU, or search by name"
    )
    public Map<String, Object> getProduct(
            @McpTool.Parameter(description = "Product ID (optional)") Long productId,
            @McpTool.Parameter(description = "Product SKU (optional)") String sku,
            @McpTool.Parameter(description = "Search by product name (optional)") String name) {
        
        try {
            if (productId != null) {
                ProductDto product = productService.getProductById(productId);
                return Map.of("success", true, "product", product);
            } else if (sku != null) {
                ProductDto product = productService.getProductBySku(sku);
                return Map.of("success", true, "product", product);
            } else if (name != null) {
                List<ProductDto> products = productService.searchProductsByName(name);
                return Map.of("success", true, "products", products);
            } else {
                return Map.of(
                    "success", false,
                    "message", "Either product ID, SKU, or name must be provided"
                );
            }
        } catch (Exception e) {
            log.error("Error getting product", e);
            return Map.of(
                "success", false,
                "message", "Failed to get product: " + e.getMessage()
            );
        }
    }
    
    @McpTool(
        name = "get_order",
        description = "Get order information by ID or customer ID"
    )
    public Map<String, Object> getOrder(
            @McpTool.Parameter(description = "Order ID (optional if customer ID provided)") Long orderId,
            @McpTool.Parameter(description = "Customer ID to get all orders (optional)") Long customerId) {
        
        try {
            if (orderId != null) {
                OrderDto order = orderService.getOrderById(orderId);
                return Map.of("success", true, "order", order);
            } else if (customerId != null) {
                List<OrderDto> orders = orderService.getOrdersByCustomerId(customerId);
                return Map.of("success", true, "orders", orders);
            } else {
                return Map.of(
                    "success", false,
                    "message", "Either order ID or customer ID must be provided"
                );
            }
        } catch (Exception e) {
            log.error("Error getting order", e);
            return Map.of(
                "success", false,
                "message", "Failed to get order: " + e.getMessage()
            );
        }
    }
    
    private List<OrderDto.OrderItemDto> parseOrderItems(String orderItemsJson) {
        // Simple parser for format: "productId1:quantity1,productId2:quantity2"
        return java.util.Arrays.stream(orderItemsJson.split(","))
                .map(item -> {
                    String[] parts = item.trim().split(":");
                    OrderDto.OrderItemDto orderItem = new OrderDto.OrderItemDto();
                    orderItem.setProductId(Long.parseLong(parts[0].trim()));
                    orderItem.setQuantity(Integer.parseInt(parts[1].trim()));
                    return orderItem;
                })
                .collect(java.util.stream.Collectors.toList());
    }
}
