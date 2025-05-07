package org.mrshoffen.tasktracker.task.advice;


import lombok.extern.slf4j.Slf4j;
import org.mrshoffen.tasktracker.commons.web.exception.AccessDeniedException;
import org.mrshoffen.tasktracker.commons.web.exception.EntityAlreadyExistsException;
import org.mrshoffen.tasktracker.commons.web.exception.EntityNotFoundException;
import org.mrshoffen.tasktracker.task.exception.TaskAlreadyExistsException;
import org.mrshoffen.tasktracker.task.exception.TaskStructureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@Slf4j
@RestControllerAdvice
public class DeskControllerAdvice {

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ProblemDetail>> handleValidationErrors(WebExchangeBindException e) {
        String errors = e.getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(" | "));
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, errors);
        problemDetail.setTitle("Validation error");
        return Mono.just(ResponseEntity.badRequest().body(problemDetail));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Mono<ResponseEntity<ProblemDetail>> handleTaskStructureException(AccessDeniedException e) {
        ProblemDetail problem = generateProblemDetail(FORBIDDEN, e);
        return Mono.just(ResponseEntity.status(FORBIDDEN).body(problem));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public Mono<ResponseEntity<ProblemDetail>> handleTaskNotFoundException(EntityNotFoundException e) {
        ProblemDetail problem = generateProblemDetail(NOT_FOUND, e);
        return Mono.just(ResponseEntity.status(NOT_FOUND).body(problem));
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public Mono<ResponseEntity<ProblemDetail>> handleTaskAlreadyExistsException(EntityAlreadyExistsException e) {
        ProblemDetail problem = generateProblemDetail(CONFLICT, e);
        return Mono.just(ResponseEntity.status(CONFLICT).body(problem));
    }

    private ProblemDetail generateProblemDetail(HttpStatus status, Exception ex) {
        log.warn("Error occured: {}", ex.getMessage());

        var problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        problemDetail.setTitle(status.getReasonPhrase());
        return problemDetail;
    }

}
