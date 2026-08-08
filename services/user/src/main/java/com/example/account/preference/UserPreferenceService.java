package com.example.account.preference;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.account.preference.dto.UserPreferenceCreateRequest;
import com.example.account.preference.dto.UserPreferencePatchRequest;
import com.example.account.preference.dto.UserPreferenceResponse;
import com.example.account.preference.dto.UserPreferenceSummaryResponse;
import com.example.account.preference.exception.UserPreferenceAlreadyExistsException;
import com.example.account.preference.exception.UserPreferenceNotFoundException;
import com.example.account.user.User;
import com.example.account.user.UserRepository;
import com.example.account.user.exception.UserNotFoundException;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserPreferenceService {

    private final UserPreferenceRepository preferenceRepository;
    private final UserRepository userRepository;
    private final UserPreferenceMapper preferenceMapper;
    private final EntityManager entityManager;

    @Cacheable(value = "userPreferenceDetails", key = "#userId")
    public List<UserPreferenceResponse> findUserPreferences(Long userId) {
        validateUserExists(userId);

        return preferenceRepository.findByUserId(userId)
            .stream()
            .map(preferenceMapper::toDto)
            .toList();
    }

    @Cacheable(value = "userPreferenceSummaries", key = "#userId")
    public List<UserPreferenceSummaryResponse> findUserPreferencesSummary(Long userId) {
        validateUserExists(userId);

        return preferenceRepository.findByUserId(userId)
            .stream()
            .map(preferenceMapper::toSummaryDto)
            .toList();
    }

    public UserPreferenceResponse findPreference(
            Long userId,
            Long preferenceId) {

        UserPreference preference = preferenceRepository
            .findByIdAndUserId(preferenceId, userId)
            .orElseThrow(() -> new UserPreferenceNotFoundException(preferenceId));

        return preferenceMapper.toDto(preference);
    }

    @CacheEvict(value = {
        "userPreferenceDetails",
        "userPreferenceSummaries"
    }, key = "#userId")
    @Transactional
    public UserPreferenceResponse createPreference(
            Long userId,
            UserPreferenceCreateRequest request) {

        if (preferenceRepository.existsByUserIdAndCategoryAndChannel(
                userId,
                request.category(),
                request.channel())) {
            throw new UserPreferenceAlreadyExistsException(
                request.category(),
                request.channel()
            );
        }

        UserPreference preference = preferenceMapper.toEntity(request);
        preference.setUser(entityManager.getReference(User.class, userId));

        return preferenceMapper.toDto(
            preferenceRepository.save(preference)
        );
    }

    @CacheEvict(value = {
        "userPreferenceDetails",
        "userPreferenceSummaries"
    }, key = "#userId")
    @Transactional
    public UserPreferenceResponse patchPreference(
            Long userId,
            Long preferenceId,
            UserPreferencePatchRequest request) {

        UserPreference preference =
            preferenceRepository.findByIdAndUserId(preferenceId, userId)
                .orElseThrow(() ->
                    new UserPreferenceNotFoundException(preferenceId));

        preferenceMapper.patchEntity(request, preference);
        preference = preferenceRepository.save(preference);

        return preferenceMapper.toDto(preference);
    }

    private void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
    }
}
