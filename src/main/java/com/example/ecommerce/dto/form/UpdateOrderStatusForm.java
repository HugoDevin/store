package com.example.ecommerce.dto.form;

import com.example.ecommerce.model.OrderStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateOrderStatusForm {
    @NotNull
    private OrderStatus status;
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
}
