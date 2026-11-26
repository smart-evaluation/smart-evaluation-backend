package com.smartexam.user.service.impl;

import com.smartexam.auth.service.RefreshTokenService;
import com.smartexam.user.dto.ChangePasswordRequest;
import com.smartexam.user.dto.DeactivateAccountRequest;
import com.smartexam.user.dto.UpdateUserProfileRequest;
import com.smartexam.user.dto.UserProfileResponse;
import com.smartexam.user.entity.User;
import com.smartexam.user.repository.RefreshTokenRepository;
import com.smartexam.user.repository.UserRepository;
import com.smartexam.user.service.UserService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    public UserServiceImpl(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository,
            PasswordEncoder passwordEncoder, RefreshTokenService refreshTokenService) {
        this.refreshTokenRepository = refreshTokenRepository;

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
    }
    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getMyProfile(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        return new UserProfileResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getMobile(),
                user.getRoles().stream()
                        .map(role -> role.getName())
                        .collect(Collectors.toSet()),
                user.getEnabled(),
                user.getCreatedAt()
        );
    }

    @Override
    @Transactional
    public UserProfileResponse updateMyProfile(
            String email,
            UpdateUserProfileRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));
        String mobile = request.mobile();
        if (mobile != null && mobile.isBlank()) {
            mobile = null;
        }
        if (mobile != null &&
                userRepository.existsByMobileAndIdNot(mobile, user.getId())) {
            throw new IllegalArgumentException(
                    "Mobile number is already registered"
            );
        }

        user.setFullName(request.fullName().trim());
        user.setMobile(mobile);
        User updatedUser = userRepository.save(user);
        return new UserProfileResponse(
                updatedUser.getId(),
                updatedUser.getFullName(),
                updatedUser.getEmail(),
                updatedUser.getMobile(),
                updatedUser.getRoles().stream()
                        .map(role -> role.getName())
                        .collect(java.util.stream.Collectors.toSet()),
                updatedUser.getEnabled(),
                updatedUser.getCreatedAt()
        );
    }

    @Override
    @Transactional
    public void changePassword(
            String email,
            ChangePasswordRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));
        // Verify current password
        if (!passwordEncoder.matches(
                request.currentPassword(),
                user.getPasswordHash())) {

            throw new IllegalArgumentException(
                    "Current password is incorrect");
        }
        // Validate new password confirmation
        if (!request.newPassword().equals(request.confirmPassword())) {

            throw new IllegalArgumentException(
                    "New password and confirm password do not match");
        }
        // Prevent reusing the current password
        if (passwordEncoder.matches(
                request.newPassword(),
                user.getPasswordHash())) {

            throw new IllegalArgumentException(
                    "New password must be different from current password");
        }
        // Encode and save new password
        user.setPasswordHash(
                passwordEncoder.encode(request.newPassword()));

        userRepository.save(user);
    }
    @Transactional
    public void revokeAllForUser(Long userId) {
        refreshTokenRepository.revokeAllByUserId(userId);
    }

    @Override
    @Transactional
    public void deactivateMyAccount(
            String email,
            DeactivateAccountRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));
        // Verify current password
        if (!passwordEncoder.matches(
                request.currentPassword(),
                user.getPasswordHash())) {

            throw new IllegalArgumentException(
                    "Current password is incorrect");
        }
        // Prevent repeated deactivation
        if (!Boolean.TRUE.equals(user.getEnabled())) {
            throw new IllegalArgumentException(
                    "Account is already deactivated");
        }
        // Disable account
        user.setEnabled(false);
        user.setAccountNonLocked(false);
        userRepository.save(user);
        // Revoke all refresh tokens
        refreshTokenService.revokeAllForUser(user.getId());
    }

}