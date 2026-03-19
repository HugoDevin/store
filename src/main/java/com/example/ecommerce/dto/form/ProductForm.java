package com.example.ecommerce.dto.form;

import com.example.ecommerce.model.ProductStatus;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class ProductForm {
    private Long id;
    private Integer version;
    @NotBlank @Size(max = 200)
    private String name;
    @Size(max = 2000)
    private String description;
    @NotNull @DecimalMin("0.0")
    private BigDecimal price;
    @NotNull @Min(0)
    private Integer stock;
    private ProductStatus status = ProductStatus.ACTIVE;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public ProductStatus getStatus() { return status; }
    public void setStatus(ProductStatus status) { this.status = status; }
}
