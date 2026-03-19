package com.example.ecommerce.dto.request;

import com.example.ecommerce.model.RoleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(@NotBlank @Size(max = 50) String username,
                              @NotBlank @Size(min = 4, max = 100) String password,
                              @NotNull RoleType role) {}
