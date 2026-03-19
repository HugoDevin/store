package com.example.ecommerce.dto.request;

import com.example.ecommerce.model.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public record CreateOrderRequest(@NotEmpty List<@Valid ItemRequest> items,
                                 @NotBlank @Size(max = 500) String shippingAddress,
                                 @NotNull PaymentMethod paymentMethod) {
    public record ItemRequest(@NotNull Long productId, @NotNull @Min(1) Integer quantity) {}
}
