package com.example.account.user;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.example.account.user.dto.UserCreateRequest;
import com.example.account.user.dto.UserPatchRequest;
import com.example.account.user.dto.UserResponse;
import com.example.account.user.dto.UserSummaryResponse;
import com.example.account.user.dto.UserUpdateRequest;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toDto(User user);

    UserSummaryResponse toSummaryDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    User toEntity(UserCreateRequest request);

    // Full update (PUT)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(UserUpdateRequest request, @MappingTarget User user);

    // Partial update (PATCH)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void patchEntity(UserPatchRequest request, @MappingTarget User user);
}
