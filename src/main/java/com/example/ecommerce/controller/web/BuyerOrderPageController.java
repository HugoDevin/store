package com.example.ecommerce.controller.web;

import com.example.ecommerce.dto.form.CheckoutForm;
import com.example.ecommerce.dto.request.CreateOrderRequest;
import com.example.ecommerce.dto.response.OrderResponse;
import com.example.ecommerce.dto.response.ProductResponse;
import com.example.ecommerce.service.OrderService;
import com.example.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/buyer")
public class BuyerOrderPageController {
    private final ProductService productService;
    private final OrderService orderService;
    public BuyerOrderPageController(ProductService productService, OrderService orderService) { this.productService = productService; this.orderService = orderService; }

    @GetMapping("/checkout")
    public String checkout(@RequestParam Long productId, @RequestParam(defaultValue = "1") Integer quantity, Model model) {
        ProductResponse product = productService.getProductDetail(productId);
        CheckoutForm form = new CheckoutForm();
        form.setProductId(productId);
        form.setQuantity(quantity);
        model.addAttribute("product", product);
        model.addAttribute("checkoutForm", form);
        model.addAttribute("subtotal", product.price().multiply(java.math.BigDecimal.valueOf(quantity)));
        return "buyer/checkout";
    }

    @PostMapping("/orders")
    public String createOrder(@Valid @ModelAttribute("checkoutForm") CheckoutForm form, BindingResult bindingResult,
                              Authentication authentication, Model model) {
        ProductResponse product = productService.getProductDetail(form.getProductId());
        model.addAttribute("product", product);
        model.addAttribute("subtotal", product.price().multiply(java.math.BigDecimal.valueOf(form.getQuantity() == null ? 1 : form.getQuantity())));
        if (bindingResult.hasErrors()) return "buyer/checkout";
        OrderResponse order = orderService.createOrder(authentication.getName(), new CreateOrderRequest(
                List.of(new CreateOrderRequest.ItemRequest(form.getProductId(), form.getQuantity())),
                form.getShippingAddress(), form.getPaymentMethod()));
        return "redirect:/buyer/orders/" + order.id() + "?created";
    }

    @GetMapping("/orders")
    public String orders(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
                         Authentication authentication, Model model) {
        Page<OrderResponse> orders = orderService.getBuyerOrders(authentication.getName(), page, size);
        model.addAttribute("orders", orders);
        return "buyer/orders";
    }

    @GetMapping("/orders/{id}")
    public String orderDetail(@PathVariable Long id, Authentication authentication, Model model) {
        model.addAttribute("order", orderService.getBuyerOrderDetail(authentication.getName(), id));
        return "buyer/order-detail";
    }

    @PostMapping("/orders/{id}/cancel")
    public String cancel(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        orderService.cancelOrder(authentication.getName(), id);
        redirectAttributes.addFlashAttribute("successMessage", "Order cancelled successfully");
        return "redirect:/buyer/orders/" + id;
    }
}
