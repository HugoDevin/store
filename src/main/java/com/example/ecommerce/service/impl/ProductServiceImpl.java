package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.request.CreateProductRequest;
import com.example.ecommerce.dto.request.UpdateProductRequest;
import com.example.ecommerce.dto.response.ProductResponse;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.exception.UnauthorizedException;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.ProductStatus;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.AuthService;
import com.example.ecommerce.service.ProductService;
import java.util.List;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final AuthService authService;

    public ProductServiceImpl(ProductRepository productRepository, AuthService authService) {
        this.productRepository = productRepository;
        this.authService = authService;
    }

    @Override
    public Page<ProductResponse> getPublicProducts(String keyword, int page, int size) {
        return productRepository.searchPublicProducts(ProductStatus.ACTIVE, keyword, PageRequest.of(page, size)).map(ProductResponse::from);
    }

    @Override
    public ProductResponse getProductDetail(Long productId) { return ProductResponse.from(getOwnedOrPublicProduct(productId)); }

    @Override
    @Transactional
    public ProductResponse createProduct(String sellerUsername, CreateProductRequest request) {
        User seller = authService.getUserByUsername(sellerUsername);
        Product product = new Product();
        product.setSeller(seller);
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock());
        product.setStatus(ProductStatus.ACTIVE);
        return ProductResponse.from(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long productId, String sellerUsername, UpdateProductRequest request, Integer version) {
        Product product = getOwnedProduct(productId, sellerUsername);
        if (version != null && !version.equals(product.getVersion())) throw new OptimisticLockingFailureException("Product has been modified by another request");
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock());
        if (request.status() != null) product.setStatus(request.status());
        return ProductResponse.from(product);
    }

    @Override
    @Transactional
    public ProductResponse updateStatus(Long productId, String sellerUsername, ProductStatus status) {
        Product product = getOwnedProduct(productId, sellerUsername);
        product.setStatus(status);
        return ProductResponse.from(product);
    }

    @Override
    @Transactional
    public void softDelete(Long productId, String sellerUsername) { updateStatus(productId, sellerUsername, ProductStatus.INACTIVE); }

    @Override
    public List<ProductResponse> getSellerProducts(String sellerUsername) {
        User seller = authService.getUserByUsername(sellerUsername);
        return productRepository.findBySellerIdWithSeller(seller.getId()).stream().map(ProductResponse::from).toList();
    }

    private Product getOwnedProduct(Long productId, String sellerUsername) {
        Product product = getOwnedOrPublicProduct(productId);
        if (!product.getSeller().getUsername().equals(sellerUsername)) throw new UnauthorizedException("You can only manage your own products");
        return product;
    }

    private Product getOwnedOrPublicProduct(Long productId) {
        return productRepository.findDetailById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }
}
