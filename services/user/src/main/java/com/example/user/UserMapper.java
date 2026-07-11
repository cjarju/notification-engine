package com.example.user;

import org.springframework.stereotype.Component;

import com.example.user.dto.UserResponse;
import com.example.user.dto.UserUpdateRequest;
import com.example.user.dto.UserCreateRequest;
import com.example.user.dto.UserPatchRequest;

@Component
class UserMapper {

    public UserResponse toDto(User user) {
        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getPhoneNumber(),
            user.getActive(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }

    public User toEntity(UserCreateRequest request) {
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPhoneNumber(request.phoneNumber());
        return user;
    }

    // Full update (PUT)
    public void updateEntity(UserUpdateRequest request, User user) {
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPhoneNumber(request.phoneNumber());
        user.setActive(request.active());
    }

    // Partial update (PATCH)
    public void patchEntity(UserPatchRequest request, User user) {
        if (request.username() != null) {
            user.setUsername(request.username());
        }

        if (request.email() != null) {
            user.setEmail(request.email());
        }

        if (request.phoneNumber() != null) {
            user.setPhoneNumber(request.phoneNumber());
        }

        if (request.active() != null) {
            user.setActive(request.active());
        }
    }
}
