package com.example.ecommerce.controller.api;

import com.example.ecommerce.dto.request.CreateProductRequest;
import com.example.ecommerce.dto.request.UpdateProductRequest;
import com.example.ecommerce.dto.response.ApiResponse;
import com.example.ecommerce.dto.response.ProductResponse;
import com.example.ecommerce.model.ProductStatus;
import com.example.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
public class ProductApiController {
    private final ProductService productService;
    public ProductApiController(ProductService productService) { this.productService = productService; }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> list(@RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size,
                                                                   @RequestParam(defaultValue = "") String keyword) {
        return ResponseEntity.ok(ApiResponse.success(productService.getPublicProducts(keyword, page, size)));
    }

    @GetMapping("/{pid}")
    public ResponseEntity<ApiResponse<ProductResponse>> detail(@PathVariable Long pid) {
        return ResponseEntity.ok(ApiResponse.success(productService.getProductDetail(pid)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> create(@Valid @RequestBody CreateProductRequest request, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(productService.createProduct(authentication.getName(), request)));
    }

    @PutMapping("/{pid}")
    public ResponseEntity<ApiResponse<ProductResponse>> update(@PathVariable Long pid, @RequestHeader(value = "If-Match", required = false) Integer version,
                                                               @Valid @RequestBody UpdateProductRequest request, Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(productService.updateProduct(pid, authentication.getName(), request, version)));
    }

    @PatchMapping("/{pid}/status")
    public ResponseEntity<ApiResponse<ProductResponse>> status(@PathVariable Long pid, @RequestBody java.util.Map<String, String> body, Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(productService.updateStatus(pid, authentication.getName(), ProductStatus.valueOf(body.get("status")))));
    }

    @DeleteMapping("/{pid}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long pid, Authentication authentication) {
        productService.softDelete(pid, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("product inactivated"));
    }
}
