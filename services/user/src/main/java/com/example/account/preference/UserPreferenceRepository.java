package com.example.account.preference;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.account.preference.enums.AlertCategory;
import com.example.account.preference.enums.DeliveryChannel;

public interface UserPreferenceRepository
        extends JpaRepository<UserPreference, Long> {

    @EntityGraph(attributePaths = "user")
    List<UserPreference> findByUserId(Long userId);

    @EntityGraph(attributePaths = "user")
    Optional<UserPreference> findByIdAndUserId(
        Long id,
        Long userId
    );

    @EntityGraph(attributePaths = "user")
    List<UserPreference> findByUserIdAndChannel(Long userId, DeliveryChannel channel);

    @EntityGraph(attributePaths = "user")
    Optional<UserPreference> findByUserIdAndCategoryAndChannel(
        Long userId,
        AlertCategory category,
        DeliveryChannel channel
    );

    boolean existsByUserIdAndCategoryAndChannel(
        Long userId,
        AlertCategory category,
        DeliveryChannel channel
    );
}
