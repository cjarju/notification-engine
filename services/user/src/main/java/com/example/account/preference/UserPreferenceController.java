package com.example.account.preference;

import java.util.List;

import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.account.common.constants.ApiPaths;
import com.example.account.common.enums.ProjectionType;
import com.example.account.preference.dto.UserPreferenceCreateRequest;
import com.example.account.preference.dto.UserPreferencePatchRequest;
import com.example.account.preference.dto.UserPreferenceResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiPaths.USERS + "/{userId}/preferences")
@RequiredArgsConstructor
public class UserPreferenceController {

    private final UserPreferenceService preferenceService;
    private final UserPreferenceModelAssembler assembler;

    @GetMapping
    public List<?> getUserPreferences(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "SUMMARY") ProjectionType projection) {

        return switch (projection) {
            case DETAIL -> preferenceService.findUserPreferences(userId)
                    .stream()
                    .map(assembler::toModel)
                    .toList();

            case SUMMARY -> preferenceService.findUserPreferencesSummary(userId);
        };
    }

    @GetMapping("/{preferenceId}")
    public EntityModel<UserPreferenceResponse> getPreference(
            @PathVariable Long userId,
            @PathVariable Long preferenceId) {

        return assembler.toModel(
            preferenceService.findPreference(userId, preferenceId)
        );
    }

    @PostMapping
    public ResponseEntity<EntityModel<UserPreferenceResponse>> createPreference(
            @PathVariable Long userId,
            @Valid @RequestBody UserPreferenceCreateRequest request) {

        UserPreferenceResponse response =
                preferenceService.createPreference(userId, request);

        EntityModel<UserPreferenceResponse> model =
                assembler.toModel(response);

        return ResponseEntity
                .created(model.getRequiredLink("self").toUri())
                .body(model);
    }

    @PatchMapping("/{preferenceId}")
    public EntityModel<UserPreferenceResponse> patchPreference(
            @PathVariable Long userId,
            @PathVariable Long preferenceId,
            @Valid @RequestBody UserPreferencePatchRequest request) {

        return assembler.toModel(
                preferenceService.patchPreference(userId, preferenceId, request)
        );
    }
}
