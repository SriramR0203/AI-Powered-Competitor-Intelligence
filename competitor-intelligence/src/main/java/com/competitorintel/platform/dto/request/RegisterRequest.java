package com.competitorintel.platform.dto.request;

import com.competitorintel.platform.domain.enums.UserRole;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Set;

@Data
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50)
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username may only contain letters, digits and underscores")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email")
    @Size(max = 255)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
             message = "Password must contain uppercase, lowercase, digit and special character")
    private String password;

    @Size(max = 100)
    private String firstName;

    @Size(max = 100)
    private String lastName;

    private Set<UserRole> roles;
}
