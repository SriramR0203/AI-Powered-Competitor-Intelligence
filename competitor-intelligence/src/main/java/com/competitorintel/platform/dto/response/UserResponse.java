package com.competitorintel.platform.dto.response;

import com.competitorintel.platform.domain.enums.UserRole;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class UserResponse {
    private Long          id;
    private String        username;
    private String        email;
    private String        firstName;
    private String        lastName;
    private String        fullName;
    private Set<UserRole> roles;
    private boolean       active;
    private boolean       emailVerified;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
}
