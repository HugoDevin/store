package com.example.ecommerce.dto.response;

import com.example.ecommerce.model.Product;
import java.math.BigDecimal;

public record ProductResponse(Long id, String name, String description, BigDecimal price, Integer stock,
                              String status, String sellerName, Integer version) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getDescription(), product.getPrice(),
                product.getStock(), product.getStatus().name(), product.getSeller().getUsername(), product.getVersion());
    }
}
