package com.example.ecommerce.dto.form;

import com.example.ecommerce.model.PaymentMethod;
import jakarta.validation.constraints.*;

public class CheckoutForm {
    @NotNull
    private Long productId;
    @NotNull @Min(1)
    private Integer quantity = 1;
    @NotBlank @Size(max = 500)
    private String shippingAddress;
    @NotNull
    private PaymentMethod paymentMethod = PaymentMethod.CASH_ON_DELIVERY;
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
}
