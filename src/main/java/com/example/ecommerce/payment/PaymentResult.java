package com.example.ecommerce.payment;

public record PaymentResult(boolean success, String message) {
    public static PaymentResult success(String message) { return new PaymentResult(true, message); }
    public static PaymentResult failed(String message) { return new PaymentResult(false, message); }
}
