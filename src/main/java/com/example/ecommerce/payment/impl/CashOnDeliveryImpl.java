package com.example.ecommerce.payment.impl;

import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.PaymentMethod;
import com.example.ecommerce.payment.PaymentGateway;
import com.example.ecommerce.payment.PaymentResult;
import org.springframework.stereotype.Component;

@Component
public class CashOnDeliveryImpl implements PaymentGateway {
    @Override
    public boolean supports(PaymentMethod paymentMethod) { return paymentMethod == PaymentMethod.CASH_ON_DELIVERY; }

    @Override
    public PaymentResult process(Order order) { return PaymentResult.success("Cash on delivery order accepted"); }
}
