package com.example.account.user;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.account.common.dto.PageResponse;
import com.example.account.common.querysupport.PageResponseMapper;
import com.example.account.user.dto.UserCreateRequest;
import com.example.account.user.dto.UserPatchRequest;
import com.example.account.user.dto.UserResponse;
import com.example.account.user.dto.UserSummaryResponse;
import com.example.account.user.dto.UserUpdateRequest;
import com.example.account.user.exception.UserAlreadyExistsException;
import com.example.account.user.exception.UserNotFoundException;

import jakarta.transaction.Transactional;


@Service
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    public UserService(UserRepository repository, UserMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public PageResponse<UserResponse>findUsers(
        UserSearchCriteria criteria,
        Pageable pageable) {

        return PageResponseMapper.map(
                findPage(criteria, pageable),
                mapper::toDto
        );
    }

    public PageResponse<UserSummaryResponse>findSummaryUsers(
        UserSearchCriteria criteria,
        Pageable pageable) {

        return PageResponseMapper.map(
                findPage(criteria, pageable),
                mapper::toSummaryDto
        );
    }

    public UserResponse findById(Long id) {
        User user = getUser(id);

        return mapper.toDto(user);
    }

    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        User user = mapper.toEntity(request);
        user = save(user, request.email());

        return mapper.toDto(user);
    }

    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = getUser(id);
        mapper.updateEntity(request, user);
        user = save(user, request.email());

        return mapper.toDto(user);
    }

    @Transactional
    public UserResponse patchUser(Long id, UserPatchRequest request) {
        User user = getUser(id);
        mapper.patchEntity(request, user);
        user = save(user, user.getEmail());

        return mapper.toDto(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = getUser(id);

        repository.delete(user);
    }

    // -- Private methods --

    private User getUser(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
    }

    private User save(User user, String email) {
        try {
            return repository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new UserAlreadyExistsException(email, ex);
        }
    }

    private Page<User> findPage(
        UserSearchCriteria criteria,
        Pageable pageable) {

        Specification<User> spec =
                    UserSpecification.withCriteria(criteria);

        return repository.findAll(spec, pageable);
    }
}
