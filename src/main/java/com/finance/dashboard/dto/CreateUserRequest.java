package com.finance.dashboard.dto;

import com.finance.dashboard.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserRequest {
    @NotBlank @Size(min = 3, max = 50) private String username;
    @Email @NotBlank private String email;
    @NotBlank @Size(min = 6, message = "Password must be at least 6 characters") private String password;
    @NotNull private Role role;
}
