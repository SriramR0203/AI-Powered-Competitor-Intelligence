package com.competitorintel.platform.controller;

import com.competitorintel.platform.dto.request.LoginRequest;
import com.competitorintel.platform.dto.request.RefreshTokenRequest;
import com.competitorintel.platform.dto.response.AuthResponse;
import com.competitorintel.platform.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication endpoints — all paths are publicly accessible (no JWT required).
 *
 * Mapped to /api/v1/auth/** which is declared permit-all in SecurityConfig.
 * The JwtAuthenticationFilter also skips these paths via shouldNotFilter().
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Login, token refresh and logout — no JWT required")
public class AuthController {

    private final AuthService authService;

    // ------------------------------------------------------------------ //
    //  POST /api/v1/auth/login
    // ------------------------------------------------------------------ //

    @PostMapping("/login")
    @Operation(
        summary     = "Login",
        description = "Authenticate with username/email and password. Returns accessToken and refreshToken."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login successful"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials"),
        @ApiResponse(responseCode = "400", description = "Validation error")
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // ------------------------------------------------------------------ //
    //  POST /api/v1/auth/refresh
    // ------------------------------------------------------------------ //

    @PostMapping("/refresh")
    @Operation(
        summary     = "Refresh access token",
        description = "Exchange a valid refresh token for a new access token."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Token refreshed"),
        @ApiResponse(responseCode = "400", description = "Invalid or expired refresh token")
    })
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    // ------------------------------------------------------------------ //
    //  POST /api/v1/auth/logout
    // ------------------------------------------------------------------ //

    @PostMapping("/logout")
    @Operation(
        summary     = "Logout",
        description = "Client-side logout. Tokens are stateless — invalidate them client-side by deleting from storage."
    )
    @ApiResponse(responseCode = "204", description = "Logged out successfully")
    public ResponseEntity<Void> logout() {
        // JWT tokens are stateless; logout is handled on the client by discarding the tokens.
        // If a token blacklist is needed in future, implement it here via a Redis store.
        return ResponseEntity.noContent().build();
    }
}
