package com.example.account.preference;

import com.example.account.common.TestcontainersConfig;
import com.example.account.preference.dto.UserPreferenceCreateRequest;
import com.example.account.preference.dto.UserPreferencePatchRequest;
import com.example.account.preference.dto.UserPreferenceResponse;
import com.example.account.preference.enums.AlertCategory;
import com.example.account.preference.enums.DeliveryChannel;
import com.example.account.user.User;
import com.example.account.user.UserRepository;
import com.example.account.user.UserTestDataFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "spring.cache.type=simple")
@Import(TestcontainersConfig.class)
class UserPreferenceServiceCacheTest {

    @Autowired
    private UserPreferenceService service;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPreferenceRepository preferenceRepository;

    @Autowired
    private CacheManager cacheManager;

    private User foo;

    @BeforeEach
    void setUp() {
        clearCaches();

        preferenceRepository.deleteAll();
        userRepository.deleteAll();

        foo = userRepository.save(
            UserTestDataFactory.activeUser("foo")
        );

        preferenceRepository.saveAll(List.of(
            UserPreferenceTestDataFactory.userPreference(
                foo,
                AlertCategory.SECURITY,
                DeliveryChannel.EMAIL
            ),
            UserPreferenceTestDataFactory.userPreference(
                foo,
                AlertCategory.SECURITY,
                DeliveryChannel.SMS
            )
        ));
    }

    @Test
    void findUserPreferences_whenDataChangesAfterFirstRead_returnsCachedResult() {
        Long userId = foo.getId();

        List<UserPreferenceResponse> first =
            service.findUserPreferences(userId);

        UserPreference preference =
            preferenceRepository.findByUserId(userId).getFirst();

        preference.setEnabled(false);
        preferenceRepository.saveAndFlush(preference);

        List<UserPreferenceResponse> second =
            service.findUserPreferences(userId);

        assertThat(second.getFirst().enabled())
            .isEqualTo(first.getFirst().enabled());
    }

    @Test
    void patchPreference_whenPreferencesAreCached_reloadsPreferencesAfterEviction() {
        Long userId = foo.getId();

        UserPreference preference =
            preferenceRepository.findByUserId(userId).getFirst();

        List<UserPreferenceResponse> cached =
            service.findUserPreferences(userId);

        preference.setEnabled(!cached.getFirst().enabled());
        preferenceRepository.saveAndFlush(preference);

        List<UserPreferenceResponse> stillCached =
            service.findUserPreferences(userId);

        assertThat(stillCached.getFirst().enabled())
            .isEqualTo(cached.getFirst().enabled());

        service.patchPreference(
            userId,
            preference.getId(),
            new UserPreferencePatchRequest(false)
        );

        List<UserPreferenceResponse> afterEviction =
            service.findUserPreferences(userId);

        assertThat(afterEviction.getFirst().enabled())
            .isFalse();
    }

    @Test
    void createPreference_whenPreferencesAreCached_reloadsPreferencesAfterEviction() {
        Long userId = foo.getId();

        List<UserPreferenceResponse> before =
            service.findUserPreferences(userId);

        service.createPreference(
            userId,
            new UserPreferenceCreateRequest(
                AlertCategory.SYSTEM,
                DeliveryChannel.PUSH,
                true
            )
        );

        List<UserPreferenceResponse> after =
            service.findUserPreferences(userId);

        assertThat(after)
            .hasSize(before.size() + 1);
    }

    private void clearCaches() {
        clearCache("userPreferenceDetails");
        clearCache("userPreferenceSummaries");
    }

    private void clearCache(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);

        if (cache != null) {
            cache.clear();
        }
    }
}
