
package com.smartexam.auth.service;

import com.smartexam.config.JwtProperties;
import com.smartexam.user.entity.RefreshToken;
import com.smartexam.user.entity.User;
import com.smartexam.user.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    private final SecureRandom secureRandom = new SecureRandom();

    public String createRefreshToken(User user) {

        byte[] randomBytes = new byte[64];
        secureRandom.nextBytes(randomBytes);

        String rawToken = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        RefreshToken refreshToken = RefreshToken.builder()
                .tokenHash(hashToken(rawToken))
                .user(user)
                .expiryDate(Instant.now().plusMillis(
                        jwtProperties.refreshTokenExpiration()
                ))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);

        return rawToken;
    }

    @Transactional
    public User validateAndRevoke(String rawToken) {

        RefreshToken storedToken = refreshTokenRepository
                .findByTokenHash(hashToken(rawToken))
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid refresh token"
                        )
                );

        if (storedToken.isRevoked()
                || !storedToken.getExpiryDate().isAfter(Instant.now())) {

            throw new IllegalArgumentException(
                    "Refresh token expired or revoked"
            );
        }

        storedToken.setRevoked(true);

        return storedToken.getUser();
    }

    @Transactional
    public void revoke(String rawToken) {

        RefreshToken storedToken = refreshTokenRepository
                .findByTokenHash(hashToken(rawToken))
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid refresh token"
                        )
                );

        storedToken.setRevoked(true);
    }

    private String hashToken(String token) {

        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Unable to hash refresh token", ex
            );
        }
    }

    /**
     * Revoke every refresh token belonging to a user.
     * Used during password changes and account deactivation.
     */
    @Transactional
    public void revokeAllForUser(Long userId) {

        refreshTokenRepository.revokeAllByUserId(userId);
    }
}
