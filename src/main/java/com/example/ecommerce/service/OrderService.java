package com.example.ecommerce.service;

import com.example.ecommerce.dto.request.CreateOrderRequest;
import com.example.ecommerce.dto.response.OrderResponse;
import com.example.ecommerce.model.OrderStatus;
import org.springframework.data.domain.Page;

public interface OrderService {
    OrderResponse createOrder(String buyerUsername, CreateOrderRequest request);
    Page<OrderResponse> getBuyerOrders(String buyerUsername, int page, int size);
    OrderResponse getBuyerOrderDetail(String buyerUsername, Long orderId);
    OrderResponse cancelOrder(String buyerUsername, Long orderId);
    Page<OrderResponse> getSellerOrders(String sellerUsername, int page, int size);
    OrderResponse getSellerOrderDetail(String sellerUsername, Long orderId);
    OrderResponse updateOrderStatus(String sellerUsername, Long orderId, OrderStatus status);
}
