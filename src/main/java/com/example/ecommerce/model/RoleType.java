package com.example.ecommerce.model;

public enum RoleType {
    BUYER,
    SELLER;

    public String asAuthority() {
        return "ROLE_" + name();
    }
}
