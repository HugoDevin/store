package com.example.ecommerce.dto.request;

import com.example.ecommerce.model.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(@NotNull OrderStatus status) {}
