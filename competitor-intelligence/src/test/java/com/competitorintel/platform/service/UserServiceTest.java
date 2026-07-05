package com.competitorintel.platform.service;

import com.competitorintel.platform.domain.entity.User;
import com.competitorintel.platform.domain.repository.UserRepository;
import com.competitorintel.platform.dto.request.RegisterRequest;
import com.competitorintel.platform.dto.response.UserResponse;
import com.competitorintel.platform.exception.DuplicateResourceException;
import com.competitorintel.platform.mapper.UserMapper;
import com.competitorintel.platform.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository    userRepository;
    @Mock UserMapper        userMapper;
    @Mock PasswordEncoder   passwordEncoder;

    @InjectMocks UserServiceImpl userService;

    @Test
    void register_duplicateUsername_throws() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("admin");
        req.setEmail("admin@test.com");
        req.setPassword("Admin123!");

        when(userRepository.existsByUsername("admin")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(req))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("username");

        verify(userRepository, never()).save(any());
    }

    @Test
    void register_duplicateEmail_throws() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("newuser");
        req.setEmail("exists@test.com");
        req.setPassword("Admin123!");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("exists@test.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(req))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("email");
    }

    @Test
    void register_happyPath_savesUser() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("john");
        req.setEmail("john@test.com");
        req.setPassword("John1234!");
        req.setFirstName("John");

        User saved = new User();
        saved.setId(5L);
        saved.setUsername("john");

        UserResponse resp = new UserResponse();
        resp.setId(5L);
        resp.setUsername("john");

        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@test.com")).thenReturn(false);
        when(passwordEncoder.encode("John1234!")).thenReturn("hash");
        when(userRepository.save(any(User.class))).thenReturn(saved);
        when(userMapper.toResponse(saved)).thenReturn(resp);

        UserResponse result = userService.register(req);

        assertThat(result.getId()).isEqualTo(5L);
        assertThat(result.getUsername()).isEqualTo("john");
        verify(userRepository).save(any(User.class));
    }
}
