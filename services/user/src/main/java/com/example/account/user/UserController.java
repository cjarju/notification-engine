package com.example.account.user;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.account.common.constants.ApiPaths;
import com.example.account.common.dto.PageResponse;
import com.example.account.common.enums.ProjectionType;
import com.example.account.user.dto.UserCreateRequest;
import com.example.account.user.dto.UserPatchRequest;
import com.example.account.user.dto.UserResponse;
import com.example.account.user.dto.UserUpdateRequest;

import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Pageable;

import jakarta.validation.Valid;

@RestController
@RequestMapping(ApiPaths.USERS)
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public PageResponse<?> getUsers(
            UserSearchCriteria criteria,
            @RequestParam(defaultValue = "SUMMARY") ProjectionType projection,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {

        return switch (projection) {
            case DETAIL -> service.findUsers(criteria, pageable);
            case SUMMARY -> service.findSummaryUsers(criteria, pageable);
        };
    }

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable("id") Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
        @Valid @RequestBody UserCreateRequest request
    ) {
        UserResponse response = service.createUser(request);

        URI location = URI.create(ApiPaths.USERS + "/" + response.id());

        return ResponseEntity
            .created(location)
            .body(response);
    }

    @PutMapping("/{id}")
    public UserResponse updateUser(
        @PathVariable("id") Long id,
        @Valid @RequestBody UserUpdateRequest request
    ) {
        return service.updateUser(id, request);
    }

    @PatchMapping("/{id}")
    public UserResponse patchUser(
        @PathVariable("id") Long id,
        @Valid @RequestBody UserPatchRequest request
    ) {
        return service.patchUser(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("id") Long id) {
        service.deleteUser(id);
    }
}
