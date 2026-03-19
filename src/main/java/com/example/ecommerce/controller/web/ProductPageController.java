package com.example.ecommerce.controller.web;

import com.example.ecommerce.dto.form.CheckoutForm;
import com.example.ecommerce.dto.response.ProductResponse;
import com.example.ecommerce.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ProductPageController {
    private final ProductService productService;
    public ProductPageController(ProductService productService) { this.productService = productService; }

    @GetMapping("/products")
    public String list(@RequestParam(defaultValue = "") String keyword,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "6") int size,
                       Model model) {
        Page<ProductResponse> products = productService.getPublicProducts(keyword, page, size);
        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        return "product/list";
    }

    @GetMapping("/products/{id}")
    public String detail(@PathVariable Long id, Model model, Authentication authentication) {
        ProductResponse product = productService.getProductDetail(id);
        CheckoutForm checkoutForm = new CheckoutForm();
        checkoutForm.setProductId(product.id());
        model.addAttribute("product", product);
        model.addAttribute("checkoutForm", checkoutForm);
        model.addAttribute("isBuyer", authentication != null && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_BUYER")));
        return "product/detail";
    }
}
