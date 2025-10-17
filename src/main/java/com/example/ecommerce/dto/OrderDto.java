package com.example.ecommerce.dto;

import com.example.ecommerce.entity.Order;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    
    private Long id;
    private String orderNumber;
    
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    
    @Valid
    @NotEmpty(message = "Order items are required")
    private List<OrderItemDto> orderItems;
    
    private Order.OrderStatus status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDto {
        @NotNull(message = "Product ID is required")
        private Long productId;
        
        @NotNull(message = "Quantity is required")
        private Integer quantity;
        
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
    }
}
