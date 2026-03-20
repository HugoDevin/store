package com.example.ecommerce.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CreateProductRequest(@NotBlank @Size(max = 200) String name,
                                   @Size(max = 2000) String description,
                                   @NotNull @DecimalMin("0.0") BigDecimal price,
                                   @NotNull @Min(0) Integer stock) {}
