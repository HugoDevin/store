package com.example.ecommerce.dto.form;

import com.example.ecommerce.model.RoleType;
import jakarta.validation.constraints.*;

public class RegisterForm {
    @NotBlank @Size(max = 50)
    private String username;
    @NotBlank @Size(min = 4, max = 100)
    private String password;
    @NotBlank @Size(min = 4, max = 100)
    private String confirmPassword;
    @NotNull
    private RoleType role;
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
    public RoleType getRole() { return role; }
    public void setRole(RoleType role) { this.role = role; }
}
