package com.example.ecommerce.service;

import com.example.ecommerce.dto.request.CreateProductRequest;
import com.example.ecommerce.dto.request.UpdateProductRequest;
import com.example.ecommerce.dto.response.ProductResponse;
import com.example.ecommerce.model.ProductStatus;
import java.util.List;
import org.springframework.data.domain.Page;

public interface ProductService {
    Page<ProductResponse> getPublicProducts(String keyword, int page, int size);
    ProductResponse getProductDetail(Long productId);
    ProductResponse createProduct(String sellerUsername, CreateProductRequest request);
    ProductResponse updateProduct(Long productId, String sellerUsername, UpdateProductRequest request, Integer version);
    ProductResponse updateStatus(Long productId, String sellerUsername, ProductStatus status);
    void softDelete(Long productId, String sellerUsername);
    List<ProductResponse> getSellerProducts(String sellerUsername);
}
