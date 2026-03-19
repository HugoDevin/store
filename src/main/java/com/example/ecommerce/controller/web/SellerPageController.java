package com.example.ecommerce.controller.web;

import com.example.ecommerce.dto.form.ProductForm;
import com.example.ecommerce.dto.form.UpdateOrderStatusForm;
import com.example.ecommerce.dto.request.CreateProductRequest;
import com.example.ecommerce.dto.request.UpdateProductRequest;
import com.example.ecommerce.model.ProductStatus;
import com.example.ecommerce.service.OrderService;
import com.example.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/seller")
public class SellerPageController {
    private final ProductService productService;
    private final OrderService orderService;
    public SellerPageController(ProductService productService, OrderService orderService) { this.productService = productService; this.orderService = orderService; }

    @GetMapping("/products")
    public String products(Authentication authentication, Model model) {
        model.addAttribute("products", productService.getSellerProducts(authentication.getName()));
        return "seller/product-list";
    }

    @GetMapping("/products/new")
    public String newProduct(Model model) {
        model.addAttribute("productForm", new ProductForm());
        model.addAttribute("isEdit", false);
        return "seller/product-form";
    }

    @PostMapping("/products")
    public String createProduct(@Valid @ModelAttribute ProductForm productForm, BindingResult bindingResult,
                                Authentication authentication, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) { model.addAttribute("isEdit", false); return "seller/product-form"; }
        productService.createProduct(authentication.getName(), new CreateProductRequest(productForm.getName(), productForm.getDescription(), productForm.getPrice(), productForm.getStock()));
        redirectAttributes.addFlashAttribute("successMessage", "Product created successfully");
        return "redirect:/seller/products";
    }

    @GetMapping("/products/{id}/edit")
    public String editPage(@PathVariable Long id, Authentication authentication, Model model) {
        var product = productService.getSellerProducts(authentication.getName()).stream().filter(p -> p.id().equals(id)).findFirst().orElseThrow();
        ProductForm form = new ProductForm();
        form.setId(product.id()); form.setVersion(product.version()); form.setName(product.name()); form.setDescription(product.description()); form.setPrice(product.price()); form.setStock(product.stock()); form.setStatus(ProductStatus.valueOf(product.status()));
        model.addAttribute("productForm", form);
        model.addAttribute("isEdit", true);
        return "seller/product-form";
    }

    @PostMapping("/products/{id}/edit")
    public String edit(@PathVariable Long id, @Valid @ModelAttribute ProductForm productForm, BindingResult bindingResult,
                       Authentication authentication, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) { model.addAttribute("isEdit", true); return "seller/product-form"; }
        productService.updateProduct(id, authentication.getName(), new UpdateProductRequest(productForm.getName(), productForm.getDescription(), productForm.getPrice(), productForm.getStock(), productForm.getStatus()), productForm.getVersion());
        redirectAttributes.addFlashAttribute("successMessage", "Product updated successfully");
        return "redirect:/seller/products";
    }

    @PostMapping("/products/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam ProductStatus status, Authentication authentication, RedirectAttributes redirectAttributes) {
        productService.updateStatus(id, authentication.getName(), status);
        redirectAttributes.addFlashAttribute("successMessage", "Product status updated");
        return "redirect:/seller/products";
    }

    @GetMapping("/orders")
    public String orders(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
                         Authentication authentication, Model model) {
        Page<?> orders = orderService.getSellerOrders(authentication.getName(), page, size);
        model.addAttribute("orders", orders);
        return "seller/order-list";
    }

    @GetMapping("/orders/{id}")
    public String orderDetail(@PathVariable Long id, Authentication authentication, Model model) {
        model.addAttribute("order", orderService.getSellerOrderDetail(authentication.getName(), id));
        UpdateOrderStatusForm form = new UpdateOrderStatusForm();
        model.addAttribute("updateOrderStatusForm", form);
        return "seller/order-detail";
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(@PathVariable Long id, @Valid @ModelAttribute UpdateOrderStatusForm updateOrderStatusForm,
                                    BindingResult bindingResult, Authentication authentication, RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("order", orderService.getSellerOrderDetail(authentication.getName(), id));
            return "seller/order-detail";
        }
        orderService.updateOrderStatus(authentication.getName(), id, updateOrderStatusForm.getStatus());
        redirectAttributes.addFlashAttribute("successMessage", "Order status updated");
        return "redirect:/seller/orders/" + id;
    }
}
