package com.smartexam.user.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record UserProfileResponse(
        Long userId,
        String fullName,
        String email,
        String mobile,
        Set<String> roles,
        Boolean enabled,
        LocalDateTime createdAt
) {
}