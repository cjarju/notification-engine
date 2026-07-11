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
        User user = repository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));

        return mapper.toDto(user);
    }

    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        User user = mapper.toEntity(request);

        try {
            user = repository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new UserAlreadyExistsException(request.email(), ex);
        }
        
        return mapper.toDto(user);
    }

    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = repository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));

        mapper.updateEntity(request, user);

        try {
            user = repository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new UserAlreadyExistsException(request.email(), ex);
        }

        return mapper.toDto(user);
    }

    @Transactional
    public UserResponse patchUser(Long id, UserPatchRequest request) {
        User user = repository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));

        mapper.patchEntity(request, user);

        try {
            user = repository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new UserAlreadyExistsException(user.getEmail(), ex);
        }

        return mapper.toDto(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = repository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));

        repository.delete(user);
    }
}
