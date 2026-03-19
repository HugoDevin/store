package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.request.CreateOrderRequest;
import com.example.ecommerce.dto.response.OrderResponse;
import com.example.ecommerce.exception.BusinessException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.exception.UnauthorizedException;
import com.example.ecommerce.model.*;
import com.example.ecommerce.payment.PaymentGateway;
import com.example.ecommerce.payment.PaymentResult;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.AuthService;
import com.example.ecommerce.service.OrderService;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final AuthService authService;
    private final List<PaymentGateway> paymentGateways;

    public OrderServiceImpl(OrderRepository orderRepository, ProductRepository productRepository, AuthService authService,
                            List<PaymentGateway> paymentGateways) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.authService = authService;
        this.paymentGateways = paymentGateways;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(String buyerUsername, CreateOrderRequest request) {
        User buyer = authService.getUserByUsername(buyerUsername);
        Order order = new Order();
        order.setBuyer(buyer);
        order.setShippingAddress(request.shippingAddress());
        order.setPaymentMethod(request.paymentMethod());
        BigDecimal total = BigDecimal.ZERO;
        for (CreateOrderRequest.ItemRequest itemRequest : request.items()) {
            Product product = productRepository.findDetailById(itemRequest.productId()).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            if (product.getStatus() != ProductStatus.ACTIVE) throw new BusinessException("Inactive product cannot be ordered");
            if (product.getStock() < itemRequest.quantity()) throw new BusinessException("Insufficient stock for product: " + product.getName());
            product.setStock(product.getStock() - itemRequest.quantity());
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setProductName(product.getName());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setQuantity(itemRequest.quantity());
            orderItem.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.quantity())));
            total = total.add(orderItem.getSubtotal());
            order.addItem(orderItem);
        }
        order.setTotalAmount(total);
        PaymentGateway gateway = paymentGateways.stream().filter(it -> it.supports(request.paymentMethod())).findFirst()
                .orElseThrow(() -> new BusinessException("Unsupported payment method"));
        PaymentResult paymentResult = gateway.process(order);
        if (!paymentResult.success()) throw new BusinessException(paymentResult.message());
        return OrderResponse.from(orderRepository.save(order));
    }

    @Override
    public Page<OrderResponse> getBuyerOrders(String buyerUsername, int page, int size) {
        User buyer = authService.getUserByUsername(buyerUsername);
        return orderRepository.findByBuyerId(buyer.getId(), PageRequest.of(page, size)).map(this::hydrateAndMap);
    }

    @Override
    public OrderResponse getBuyerOrderDetail(String buyerUsername, Long orderId) {
        Order order = getOrderWithItems(orderId);
        if (!order.getBuyer().getUsername().equals(buyerUsername)) throw new UnauthorizedException("You can only view your own orders");
        return OrderResponse.from(order);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(String buyerUsername, Long orderId) {
        Order order = getOrderWithItems(orderId);
        if (!order.getBuyer().getUsername().equals(buyerUsername)) throw new UnauthorizedException("You can only cancel your own orders");
        if (order.getStatus() != OrderStatus.PENDING) throw new BusinessException("Only pending orders can be cancelled");
        order.setStatus(OrderStatus.CANCELLED);
        for (OrderItem item : order.getItems()) item.getProduct().setStock(item.getProduct().getStock() + item.getQuantity());
        return OrderResponse.from(order);
    }

    @Override
    public Page<OrderResponse> getSellerOrders(String sellerUsername, int page, int size) {
        User seller = authService.getUserByUsername(sellerUsername);
        return orderRepository.findSellerOrders(seller.getId(), PageRequest.of(page, size)).map(this::hydrateAndMap);
    }

    @Override
    public OrderResponse getSellerOrderDetail(String sellerUsername, Long orderId) {
        Order order = getOrderWithItems(orderId);
        if (!canSellerAccess(order, sellerUsername)) throw new UnauthorizedException("This order does not belong to your catalog");
        return OrderResponse.from(order);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(String sellerUsername, Long orderId, OrderStatus status) {
        Order order = getOrderWithItems(orderId);
        if (!canSellerAccess(order, sellerUsername)) throw new UnauthorizedException("This order does not belong to your catalog");
        validateTransition(order.getStatus(), status);
        order.setStatus(status);
        return OrderResponse.from(order);
    }

    private boolean canSellerAccess(Order order, String sellerUsername) {
        return order.getItems().stream().anyMatch(item -> item.getProduct().getSeller().getUsername().equals(sellerUsername));
    }

    private void validateTransition(OrderStatus current, OrderStatus next) {
        boolean valid = (current == OrderStatus.PENDING && next == OrderStatus.CONFIRMED)
                || (current == OrderStatus.CONFIRMED && next == OrderStatus.SHIPPED)
                || (current == OrderStatus.SHIPPED && next == OrderStatus.COMPLETED);
        if (!valid) throw new BusinessException("Illegal order status transition: " + current + " -> " + next);
    }

    private Order getOrderWithItems(Long orderId) {
        return orderRepository.findDetailedById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    private OrderResponse hydrateAndMap(Order order) {
        return OrderResponse.from(getOrderWithItems(order.getId()));
    }
}
