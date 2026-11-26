package com.smartexam.user.service;

import com.smartexam.user.dto.ChangePasswordRequest;
import com.smartexam.user.dto.DeactivateAccountRequest;
import com.smartexam.user.dto.UpdateUserProfileRequest;
import com.smartexam.user.dto.UserProfileResponse;

public interface UserService {

    UserProfileResponse getMyProfile(String email);

    UserProfileResponse updateMyProfile(
            String email,
            UpdateUserProfileRequest request
    );

    void changePassword(
            String email,
            ChangePasswordRequest request
    );

    void deactivateMyAccount(
            String email,
            DeactivateAccountRequest request
    );
}