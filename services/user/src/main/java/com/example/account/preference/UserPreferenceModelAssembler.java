package com.example.account.preference;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.example.account.common.enums.ProjectionType;
import com.example.account.preference.dto.UserPreferenceResponse;

@Component
public class UserPreferenceModelAssembler
        implements RepresentationModelAssembler<
                UserPreferenceResponse,
                EntityModel<UserPreferenceResponse>> {

    @Override
    public EntityModel<UserPreferenceResponse> toModel(
            UserPreferenceResponse response) {

        return EntityModel.of(
            response,
            linkTo(methodOn(UserPreferenceController.class)
                .getPreference(
                    response.userId(),
                    response.id()))
                .withSelfRel(),

            linkTo(methodOn(UserPreferenceController.class)
                .getUserPreferences(
                    response.userId(),
                    ProjectionType.SUMMARY))
                .withRel("preferences")
        );
    }
}

