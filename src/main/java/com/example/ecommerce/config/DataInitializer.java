package com.example.ecommerce.config;

import com.example.ecommerce.model.*;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, ProductRepository productRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) return;
        User buyer = new User();
        buyer.setUsername("buyer1");
        buyer.setPassword(passwordEncoder.encode("password"));
        buyer.setRole(RoleType.BUYER);
        userRepository.save(buyer);

        User seller = new User();
        seller.setUsername("seller1");
        seller.setPassword(passwordEncoder.encode("password"));
        seller.setRole(RoleType.SELLER);
        userRepository.save(seller);

        productRepository.save(createProduct(seller, "Keyboard", "Mechanical keyboard", new BigDecimal("1999"), 10, ProductStatus.ACTIVE));
        productRepository.save(createProduct(seller, "Mouse", "Gaming mouse", new BigDecimal("899"), 5, ProductStatus.ACTIVE));
        productRepository.save(createProduct(seller, "Monitor", "27 inch monitor", new BigDecimal("4999"), 0, ProductStatus.INACTIVE));
    }

    private Product createProduct(User seller, String name, String description, BigDecimal price, int stock, ProductStatus status) {
        Product product = new Product();
        product.setSeller(seller);
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setStatus(status);
        return product;
    }
}
