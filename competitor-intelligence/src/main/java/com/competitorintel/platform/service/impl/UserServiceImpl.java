package com.competitorintel.platform.service.impl;

import com.competitorintel.platform.domain.entity.User;
import com.competitorintel.platform.domain.enums.UserRole;
import com.competitorintel.platform.domain.repository.UserRepository;
import com.competitorintel.platform.dto.request.ChangePasswordRequest;
import com.competitorintel.platform.dto.request.RegisterRequest;
import com.competitorintel.platform.dto.request.UpdateUserRequest;
import com.competitorintel.platform.dto.response.PageResponse;
import com.competitorintel.platform.dto.response.UserResponse;
import com.competitorintel.platform.exception.BadRequestException;
import com.competitorintel.platform.exception.DuplicateResourceException;
import com.competitorintel.platform.exception.ResourceNotFoundException;
import com.competitorintel.platform.mapper.UserMapper;
import com.competitorintel.platform.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository  userRepository;
    private final UserMapper      userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("User", "username", request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        Set<UserRole> roles = (request.getRoles() != null && !request.getRoles().isEmpty())
                ? request.getRoles() : Set.of(UserRole.ROLE_VIEWER);

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRoles(roles);
        user.setActive(true);

        User saved = userRepository.save(user);
        log.info("Registered user '{}'", saved.getUsername());
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        return userMapper.toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String username) {
        return userMapper.toResponse(
                userRepository.findByUsername(username)
                        .orElseThrow(() -> new ResourceNotFoundException("User", "username", username)));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getAllUsers(String search, Pageable pageable) {
        Page<UserResponse> page = (StringUtils.hasText(search)
                ? userRepository.searchUsers(search, pageable)
                : userRepository.findAll(pageable))
                .map(userMapper::toResponse);
        return PageResponse.of(page);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = findById(id);
        if (StringUtils.hasText(request.getEmail()) && !request.getEmail().equals(user.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }
        if (StringUtils.hasText(request.getEmail()))     user.setEmail(request.getEmail());
        if (StringUtils.hasText(request.getFirstName())) user.setFirstName(request.getFirstName());
        if (StringUtils.hasText(request.getLastName()))  user.setLastName(request.getLastName());
        if (request.getIsActive() != null)               user.setActive(request.getIsActive());
        if (request.getRoles()    != null && !request.getRoles().isEmpty()) user.setRoles(request.getRoles());
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void changePassword(String username, ChangePasswordRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Current password is incorrect");
        }
        if (passwordEncoder.matches(request.getNewPassword(), user.getPasswordHash())) {
            throw new BadRequestException("New password must differ from current password");
        }
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Password changed for user '{}'", username);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = findById(id);
        userRepository.delete(user);
    }

    @Override @Transactional
    public void activateUser(Long id) {
        User user = findById(id);
        user.setActive(true);
        userRepository.save(user);
    }

    @Override @Transactional
    public void deactivateUser(Long id) {
        User user = findById(id);
        user.setActive(false);
        userRepository.save(user);
    }

    private User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }
}
