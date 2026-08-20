package com.example.account.user;

import com.example.account.user.dto.UserResponse;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class UserModelAssembler
        implements RepresentationModelAssembler<UserResponse, EntityModel<UserResponse>> {

    @Override
    public EntityModel<UserResponse> toModel(UserResponse user) {

        return EntityModel.of(user,
            linkTo(methodOn(UserController.class)
                    .getUser(user.id()))
                    .withSelfRel(),

            linkTo(UserController.class)
                    .withRel("users")
        );
    }
}
