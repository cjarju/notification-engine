package com.example.account.user;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.example.account.user.dto.UserResponse;

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
