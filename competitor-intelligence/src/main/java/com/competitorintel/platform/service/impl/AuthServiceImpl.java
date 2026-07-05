package com.competitorintel.platform.service.impl;

import com.competitorintel.platform.config.AppProperties;
import com.competitorintel.platform.domain.entity.User;
import com.competitorintel.platform.domain.repository.UserRepository;
import com.competitorintel.platform.dto.request.LoginRequest;
import com.competitorintel.platform.dto.request.RefreshTokenRequest;
import com.competitorintel.platform.dto.response.AuthResponse;
import com.competitorintel.platform.exception.BadRequestException;
import com.competitorintel.platform.exception.ResourceNotFoundException;
import com.competitorintel.platform.mapper.UserMapper;
import com.competitorintel.platform.security.jwt.JwtTokenProvider;
import com.competitorintel.platform.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authManager;
    private final JwtTokenProvider      tokenProvider;
    private final UserRepository        userRepository;
    private final UserMapper            userMapper;
    private final AppProperties         appProperties;

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail(), request.getPassword()));

        String accessToken  = tokenProvider.generateAccessToken(auth);
        String refreshToken = tokenProvider.generateRefreshToken(auth);

        User user = userRepository
                .findByUsernameOrEmail(request.getUsernameOrEmail(), request.getUsernameOrEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "usernameOrEmail", request.getUsernameOrEmail()));

        userRepository.updateLastLogin(user.getId(), LocalDateTime.now());
        log.info("User '{}' logged in", user.getUsername());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(appProperties.getJwt().getExpirationMs())
                .user(userMapper.toResponse(user))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String token = request.getRefreshToken();
        if (!tokenProvider.validateToken(token)) {
            throw new BadRequestException("Invalid or expired refresh token");
        }
        if (!tokenProvider.isRefreshToken(token)) {
            throw new BadRequestException("Provided token is not a refresh token");
        }

        String username = tokenProvider.getUsernameFromToken(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User not found for refresh token"));

        if (!user.isActive()) {
            throw new BadRequestException("User account is inactive");
        }

        String roles = user.getRoles().stream()
                .map(Enum::name).collect(Collectors.joining(","));
        String newAccessToken = tokenProvider.generateAccessTokenFromUsername(username, roles);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(token)
                .tokenType("Bearer")
                .expiresIn(appProperties.getJwt().getExpirationMs())
                .user(userMapper.toResponse(user))
                .build();
    }
}
