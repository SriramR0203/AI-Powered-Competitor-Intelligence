package com.competitorintel.platform.controller;

import com.competitorintel.platform.dto.request.ChangePasswordRequest;
import com.competitorintel.platform.dto.request.RegisterRequest;
import com.competitorintel.platform.dto.request.UpdateUserRequest;
import com.competitorintel.platform.dto.response.PageResponse;
import com.competitorintel.platform.dto.response.UserResponse;
import com.competitorintel.platform.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Register a new user (ADMIN only)")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(request));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user's profile")
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(userService.getCurrentUser(user.getUsername()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List all users (ADMIN only)")
    public ResponseEntity<PageResponse<UserResponse>> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0")    int page,
            @RequestParam(defaultValue = "20")   int size) {
        return ResponseEntity.ok(userService.getAllUsers(search,
                PageRequest.of(page, size, Sort.by("createdAt").descending())));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user (ADMIN only)")
    public ResponseEntity<UserResponse> update(@PathVariable Long id,
                                                @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @PatchMapping("/me/password")
    @Operation(summary = "Change own password")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal UserDetails user,
                                                @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(user.getUsername(), request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        userService.activateUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
