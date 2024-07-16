package com.team7.rupiapp.exception;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.team7.rupiapp.util.ApiResponseUtil;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<Object> handleDataNotFoundException(DataNotFoundException ex) {
        log.error(ex.getMessage());

        return ApiResponseUtil.error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error(ex.getMessage());

        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        fieldError -> fieldError.getField().replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase(),
                        DefaultMessageSourceResolvable::getDefaultMessage,
                        (existing, replacement) -> existing // merge function to handle duplicate keys
                ));

        return ApiResponseUtil.error(HttpStatus.BAD_REQUEST, "Validation failed", errors);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Object> handleIllegalStateException(IllegalStateException ex) {
        log.error(ex.getMessage());

        return ApiResponseUtil.error(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex) {
        log.error(ex.getMessage());

        return ApiResponseUtil.error(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        String requestId = UUID.randomUUID().toString();
        log.error("Error occurred with requestId: {}", requestId, ex.getMessage());

        return ApiResponseUtil.error(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", requestId);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException ex) {
        log.error(ex.getMessage());

        return ApiResponseUtil.error(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException(BadRequestException ex) {
        log.error(ex.getMessage());

        return ApiResponseUtil.error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<Object> handleExpiredJwtException(ExpiredJwtException ex) {
        log.error(ex.getMessage());

        return ApiResponseUtil.error(HttpStatus.UNAUTHORIZED, "Token has expired");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        Throwable mostSpecificCause = ex.getMostSpecificCause();
        if (mostSpecificCause instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) mostSpecificCause;
            String fieldName = ife.getPath().stream()
                    .map(reference -> reference.getFieldName())
                    .collect(Collectors.joining("."));

            String validValues = Arrays.stream(ife.getTargetType().getEnumConstants())
                    .map(Object::toString)
                    .collect(Collectors.joining(", ", "[", "]"));

            String message = "Invalid value '" + ife.getValue() + "' for " + fieldName +
                    ". Valid values are: " + validValues;

            return ApiResponseUtil.error(HttpStatus.BAD_REQUEST, message);
        }

        return ResponseEntity.badRequest().body("Invalid request body");
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Object> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        log.error(ex.getMessage());

        return ApiResponseUtil.error(HttpStatus.NOT_FOUND, ex.getMessage());
    }
}