package com.example.user;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import com.example.user.dto.UserCreateRequest;
import com.example.user.dto.UserPatchRequest;
import com.example.user.dto.UserResponse;
import com.example.user.dto.UserUpdateRequest;
import com.example.user.exception.UserAlreadyExistsException;
import com.example.user.exception.UserNotFoundException;


@Service
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    public UserService(UserRepository repository, UserMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
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

}
