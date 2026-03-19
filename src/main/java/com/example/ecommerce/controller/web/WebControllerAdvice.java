package com.example.ecommerce.controller.web;

import com.example.ecommerce.exception.GlobalExceptionHandler.PageFlowException;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(basePackages = "com.example.ecommerce.controller.web")
public class WebControllerAdvice {
    @ModelAttribute
    public void currentUser(Authentication authentication, Model model) {
        if (authentication != null && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().startsWith("ROLE_"))) {
            model.addAttribute("currentUsername", authentication.getName());
            model.addAttribute("currentRole", authentication.getAuthorities().stream().findFirst().map(a -> a.getAuthority().replace("ROLE_", "")).orElse("GUEST"));
        } else {
            model.addAttribute("currentUsername", "Guest");
            model.addAttribute("currentRole", "GUEST");
        }
    }

    @ExceptionHandler(PageFlowException.class)
    public String handlePageException(PageFlowException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }
}
