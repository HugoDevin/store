package com.example.ecommerce.controller.web;

import com.example.ecommerce.dto.form.RegisterForm;
import com.example.ecommerce.dto.request.RegisterRequest;
import com.example.ecommerce.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthPageController {
    private final AuthService authService;
    public AuthPageController(AuthService authService) { this.authService = authService; }

    @GetMapping("/login")
    public String login() { return "auth/login"; }

    @GetMapping("/register")
    public String registerPage(Model model) {
        if (!model.containsAttribute("registerForm")) model.addAttribute("registerForm", new RegisterForm());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerForm") RegisterForm form, BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "mismatch", "Confirm password must match password");
        }
        if (bindingResult.hasErrors()) return "auth/register";
        authService.register(new RegisterRequest(form.getUsername(), form.getPassword(), form.getRole()));
        redirectAttributes.addFlashAttribute("successMessage", "Registration successful. Please login.");
        return "redirect:/login";
    }
}
