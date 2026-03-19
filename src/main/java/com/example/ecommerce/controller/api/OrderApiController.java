package com.example.ecommerce.controller.api;

import com.example.ecommerce.dto.request.CreateOrderRequest;
import com.example.ecommerce.dto.request.UpdateOrderStatusRequest;
import com.example.ecommerce.dto.response.ApiResponse;
import com.example.ecommerce.dto.response.OrderResponse;
import com.example.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderApiController {
    private final OrderService orderService;
    public OrderApiController(OrderService orderService) { this.orderService = orderService; }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> create(@Valid @RequestBody CreateOrderRequest request, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(orderService.createOrder(authentication.getName(), request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> myOrders(@RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "10") int size,
                                                                     Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getBuyerOrders(authentication.getName(), page, size)));
    }

    @GetMapping("/{oid}")
    public ResponseEntity<ApiResponse<OrderResponse>> detail(@PathVariable Long oid, Authentication authentication) {
        boolean isSeller = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_SELLER"));
        OrderResponse response = isSeller ? orderService.getSellerOrderDetail(authentication.getName(), oid)
                : orderService.getBuyerOrderDetail(authentication.getName(), oid);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{oid}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(@PathVariable Long oid, @Valid @RequestBody UpdateOrderStatusRequest request,
                                                                   Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(orderService.updateOrderStatus(authentication.getName(), oid, request.status())));
    }

    @DeleteMapping("/{oid}")
    public ResponseEntity<ApiResponse<OrderResponse>> cancel(@PathVariable Long oid, Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(orderService.cancelOrder(authentication.getName(), oid)));
    }
}
