package com.competitorintel.platform.service;

import com.competitorintel.platform.dto.request.LoginRequest;
import com.competitorintel.platform.dto.request.RefreshTokenRequest;
import com.competitorintel.platform.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(RefreshTokenRequest request);
}
