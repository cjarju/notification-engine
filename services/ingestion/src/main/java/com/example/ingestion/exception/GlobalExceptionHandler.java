package com.example.ingestion.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DispatcherCommunicationException.class)
    public ProblemDetail handleDispatcherCommunication(DispatcherCommunicationException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_GATEWAY);
        problem.setTitle("Dispatcher communication error");
        problem.setDetail(ex.getMessage());
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        problem.setTitle("Validation failed");
        problem.setDetail("One or more fields are invalid");

        List<ValidationError> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ValidationError(
                        error.getField(),
                        error.getDefaultMessage()))
                .toList();

        problem.setProperty("errors", errors);

        return problem;
    }
}
