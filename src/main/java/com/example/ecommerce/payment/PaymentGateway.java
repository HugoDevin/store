package com.example.ecommerce.payment;

import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.PaymentMethod;

public interface PaymentGateway {
    boolean supports(PaymentMethod paymentMethod);
    PaymentResult process(Order order);
}
