package com.competitorintel.platform.service;

import com.competitorintel.platform.dto.request.ChangePasswordRequest;
import com.competitorintel.platform.dto.request.RegisterRequest;
import com.competitorintel.platform.dto.request.UpdateUserRequest;
import com.competitorintel.platform.dto.response.PageResponse;
import com.competitorintel.platform.dto.response.UserResponse;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponse register(RegisterRequest request);
    UserResponse getUserById(Long id);
    UserResponse getCurrentUser(String username);
    PageResponse<UserResponse> getAllUsers(String search, Pageable pageable);
    UserResponse updateUser(Long id, UpdateUserRequest request);
    void changePassword(String username, ChangePasswordRequest request);
    void deleteUser(Long id);
    void activateUser(Long id);
    void deactivateUser(Long id);
}
