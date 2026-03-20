package com.example.ecommerce.payment.impl;

import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.PaymentMethod;
import com.example.ecommerce.payment.PaymentGateway;
import com.example.ecommerce.payment.PaymentResult;
import org.springframework.stereotype.Component;

@Component
public class CreditCardImpl implements PaymentGateway {
    @Override
    public boolean supports(PaymentMethod paymentMethod) { return paymentMethod == PaymentMethod.CREDIT_CARD; }

    @Override
    public PaymentResult process(Order order) {
        return PaymentResult.failed("Credit card gateway is reserved for future integration");
    }
}
