
package com.smartexam.auth.service;

import com.smartexam.auth.dto.AuthResponse;
import com.smartexam.auth.dto.LoginRequest;
import com.smartexam.auth.dto.RefreshTokenRequest;
import com.smartexam.auth.dto.RegistrationRequest;
import com.smartexam.auth.security.JwtService;
import com.smartexam.user.entity.Role;
import com.smartexam.user.entity.User;
import com.smartexam.user.repository.RoleRepository;
import com.smartexam.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private static final String STUDENT_ROLE = "ROLE_STUDENT";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public AuthResponse register(RegistrationRequest request) {

        String email = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        String mobile = request.mobile().trim();

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Email is already registered"
            );
        }

        if (userRepository.existsByMobile(mobile)) {
            throw new IllegalArgumentException(
                    "Mobile number is already registered"
            );
        }

        Role studentRole = roleRepository.findByName(STUDENT_ROLE)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Default STUDENT role not found"
                        )
                );

        User user = User.builder()
                .fullName(request.fullName().trim())
                .email(email)
                .mobile(mobile)
                .passwordHash(
                        passwordEncoder.encode(request.password())
                )
                .enabled(true)
                .accountNonLocked(true)
                .roles(java.util.Set.of(studentRole))
                .build();

        User savedUser = userRepository.save(user);

        return createAuthResponse(savedUser);
    }

    public AuthResponse login(LoginRequest request) {

        String email = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                email,
                                request.password()
                        )
                );

        String authenticatedEmail =
                authentication.getName();

        User user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found"
                        )
                );

        return createAuthResponse(user);
    }

    private AuthResponse createAuthResponse(User user) {

        String accessToken =
                jwtService.generateAccessToken(user);

        String refreshToken =
                refreshTokenService.createRefreshToken(user);

        List<String> roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .sorted()
                .toList();

        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                roles,
                jwtService.getAccessTokenExpiration() / 1000
        );
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {

        User user = refreshTokenService.validateAndRevoke(
                request.refreshToken()
        );

        if (!Boolean.TRUE.equals(user.getEnabled())
                || !Boolean.TRUE.equals(user.getAccountNonLocked())) {
            throw new IllegalArgumentException("User account is disabled or locked");
        }

        return createAuthResponse(user);
    }

    @Transactional
    public void logout(RefreshTokenRequest request) {

        refreshTokenService.revoke(request.refreshToken());
    }
}
