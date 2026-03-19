package com.example.ecommerce.dto.response;

import com.example.ecommerce.model.Order;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(Long id, String buyerUsername, String status, String paymentMethod, String shippingAddress,
                            BigDecimal totalAmount, Integer version, LocalDateTime createdAt, List<OrderItemResponse> items) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(order.getId(), order.getBuyer().getUsername(), order.getStatus().name(),
                order.getPaymentMethod().name(), order.getShippingAddress(), order.getTotalAmount(), order.getVersion(),
                order.getCreatedAt(), order.getItems().stream().map(OrderItemResponse::from).toList());
    }
}
