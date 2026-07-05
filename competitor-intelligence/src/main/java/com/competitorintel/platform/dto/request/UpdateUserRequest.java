package com.competitorintel.platform.dto.request;

import com.competitorintel.platform.domain.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class UpdateUserRequest {

    @Email(message = "Must be a valid email")
    @Size(max = 255)
    private String email;

    @Size(max = 100)
    private String firstName;

    @Size(max = 100)
    private String lastName;

    private Boolean isActive;

    private Set<UserRole> roles;
}
