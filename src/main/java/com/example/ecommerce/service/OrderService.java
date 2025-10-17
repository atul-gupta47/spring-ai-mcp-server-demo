package com.example.ecommerce.service;

import com.example.ecommerce.dto.OrderDto;
import com.example.ecommerce.entity.*;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CustomerService customerService;
    
    public OrderDto createOrder(OrderDto orderDto) {
        // Validate customer exists
        customerService.getCustomerById(orderDto.getCustomerId());
        
        Order order = new Order();
        order.setCustomer(new Customer());
        order.getCustomer().setId(orderDto.getCustomerId());
        order.setStatus(Order.OrderStatus.PENDING);
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        for (OrderDto.OrderItemDto itemDto : orderDto.getOrderItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + itemDto.getProductId()));
            
            // Check stock availability
            if (product.getStockQuantity() < itemDto.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.calculateTotalPrice();
            
            order.getOrderItems().add(orderItem);
            totalAmount = totalAmount.add(orderItem.getTotalPrice());
            
            // Update stock
            product.setStockQuantity(product.getStockQuantity() - itemDto.getQuantity());
            productRepository.save(product);
        }
        
        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);
        
        return mapToDto(savedOrder);
    }
    
    public OrderDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        return mapToDto(order);
    }
    
    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    public List<OrderDto> getOrdersByCustomerId(Long customerId) {
        return orderRepository.findByCustomerId(customerId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    public OrderDto updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        order.setStatus(status);
        Order savedOrder = orderRepository.save(order);
        return mapToDto(savedOrder);
    }
    
    private OrderDto mapToDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setCustomerId(order.getCustomer().getId());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        
        List<OrderDto.OrderItemDto> itemDtos = order.getOrderItems().stream()
                .map(item -> {
                    OrderDto.OrderItemDto itemDto = new OrderDto.OrderItemDto();
                    itemDto.setProductId(item.getProduct().getId());
                    itemDto.setQuantity(item.getQuantity());
                    itemDto.setUnitPrice(item.getUnitPrice());
                    itemDto.setTotalPrice(item.getTotalPrice());
                    return itemDto;
                })
                .collect(Collectors.toList());
        
        dto.setOrderItems(itemDtos);
        return dto;
    }
}
