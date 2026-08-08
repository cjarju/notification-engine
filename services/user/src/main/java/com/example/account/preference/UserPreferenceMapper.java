package com.example.account.preference;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.example.account.preference.dto.UserPreferenceCreateRequest;
import com.example.account.preference.dto.UserPreferencePatchRequest;
import com.example.account.preference.dto.UserPreferenceResponse;
import com.example.account.preference.dto.UserPreferenceSummaryResponse;

@Mapper(componentModel = "spring")
public interface UserPreferenceMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    UserPreferenceResponse toDto(UserPreference userPreference);

    @Mapping(target = "username", source = "user.username")
    UserPreferenceSummaryResponse toSummaryDto(UserPreference userPreference);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "enabled", source = "enabled", defaultValue = "true")
    UserPreference toEntity(UserPreferenceCreateRequest request);

    // Partial update (PATCH)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "channel", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void patchEntity(UserPreferencePatchRequest request, @MappingTarget UserPreference userPreference);
}
