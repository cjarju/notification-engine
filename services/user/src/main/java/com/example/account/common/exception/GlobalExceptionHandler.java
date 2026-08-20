package com.example.account.common.exception;

import com.example.account.user.exception.UserAlreadyExistsException;
import com.example.account.user.exception.UserNotFoundException;
import com.example.account.preference.exception.UserPreferenceAlreadyExistsException;
import com.example.account.preference.exception.UserPreferenceNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleUserNotFoundException(UserNotFoundException ex) {

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setType(URI.create("errors/user-not-found"));
        problemDetail.setTitle("User not found");
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ProblemDetail handleUserAlreadyExistsException(UserAlreadyExistsException ex) {

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);

        problemDetail.setType(URI.create("errors/user-already-exists"));
        problemDetail.setTitle("User already exists");
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    @ExceptionHandler(UserPreferenceNotFoundException.class)
    public ProblemDetail handleUserPreferenceNotFoundException(UserPreferenceNotFoundException ex) {

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setType(URI.create("errors/user-preference-not-found"));
        problemDetail.setTitle("User preference not found");
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    @ExceptionHandler(UserPreferenceAlreadyExistsException.class)
    public ProblemDetail handleUserPreferenceAlreadyExistsException(UserPreferenceAlreadyExistsException ex) {

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problemDetail.setType(URI.create("errors/user-preference-already-exists"));
        problemDetail.setTitle("User preference already exists");
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException ex) {

        List<ValidationError> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ValidationError(
                        error.getField(),
                        error.getDefaultMessage()))
                .toList();

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setType(URI.create("errors/bad-request"));
        problemDetail.setTitle("Validation failed");
        problemDetail.setDetail("One or more fields are invalid");
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("errors", errors);

        return problemDetail;
    }
}
