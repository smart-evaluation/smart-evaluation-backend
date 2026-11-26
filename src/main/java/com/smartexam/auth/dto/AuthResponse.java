
package com.smartexam.auth.dto;

import java.util.List;

public record AuthResponse(

        String accessToken,

        String refreshToken,

        String tokenType,

        Long userId,

        String fullName,

        String email,

        List<String> roles,

        long expiresIn

) {
}
