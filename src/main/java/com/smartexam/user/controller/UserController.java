package com.smartexam.user.controller;

import com.smartexam.user.dto.ChangePasswordRequest;
import com.smartexam.user.dto.DeactivateAccountRequest;
import com.smartexam.user.dto.UpdateUserProfileRequest;
import com.smartexam.user.dto.UserProfileResponse;
import com.smartexam.user.service.UserService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(
            Authentication authentication) {

        UserProfileResponse response =
                userService.getMyProfile(authentication.getName());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateMyProfile(
            @Valid @RequestBody UpdateUserProfileRequest request,
            Authentication authentication) {

        UserProfileResponse response =
                userService.updateMyProfile(
                        authentication.getName(),
                        request
                );
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/me/change-password")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {

        userService.changePassword(
                authentication.getName(),
                request
        );
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deactivateMyAccount(
            @Valid @RequestBody DeactivateAccountRequest request,
            Authentication authentication) {

        userService.deactivateMyAccount(
                authentication.getName(),
                request
        );

        return ResponseEntity.noContent().build();
    }
}