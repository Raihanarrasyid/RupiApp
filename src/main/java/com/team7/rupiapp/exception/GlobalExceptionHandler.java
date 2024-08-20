package com.team7.rupiapp.exception;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.team7.rupiapp.service.LoggingService;
import org.slf4j.MDC;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.team7.rupiapp.util.ApiResponseUtil;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    private final LoggingService loggingService;

    public GlobalExceptionHandler(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<Object> handleDataNotFoundException(DataNotFoundException ex) {
        loggingService.catchException(ex);
        loggingService.logError(ex);

        return ApiResponseUtil.error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String message = "Validation failed";
        loggingService.catchException(ex);
        loggingService.logError(ex);

        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        fieldError -> fieldError.getField().replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase(),
                        DefaultMessageSourceResolvable::getDefaultMessage,
                        (existing, replacement) -> existing));

        return ApiResponseUtil.error(HttpStatus.UNPROCESSABLE_ENTITY, message, errors);
    }

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<Object> handleResourceConflict(ResourceConflictException ex) {
        loggingService.catchException(ex);
        loggingService.logError(ex);

        return ApiResponseUtil.error(HttpStatus.CONFLICT, ex.getMessage(), ex.getErrors());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex) {
        loggingService.catchException(ex);
        loggingService.logError(ex);

        return ApiResponseUtil.error(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        String correlationId = MDC.get("correlation_id");
        String message = String.format("Error occurred with correlation_id: %s", correlationId);
        loggingService.catchException(ex);

        return ApiResponseUtil.error(
                HttpStatus.INTERNAL_SERVER_ERROR,
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                message);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException ex) {
        if (ex.getMessage().equals("Bad credentials")) {
            String message = "Invalid username or password";
            loggingService.catchException(ex, message);
            loggingService.logError(ex);

            return ApiResponseUtil.error(HttpStatus.UNAUTHORIZED, message);
        } else {
            loggingService.catchException(ex);
            loggingService.logError(ex);
        }

        return ApiResponseUtil.error(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException(BadRequestException ex) {
        loggingService.catchException(ex);
        loggingService.logError(ex);

        return ApiResponseUtil.error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<Object> handleExpiredJwtException(ExpiredJwtException ex) {
        String message = "Token has expired";
        loggingService.catchException(ex);
        loggingService.logError(ex);

        return ApiResponseUtil.error(HttpStatus.UNAUTHORIZED, message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String message;
        Throwable mostSpecificCause = ex.getMostSpecificCause();

        if (mostSpecificCause instanceof InvalidFormatException ife) {
            String fieldName = ife.getPath().stream()
                    .map(Reference::getFieldName)
                    .collect(Collectors.joining("."));

            message = "Invalid value '" + ife.getValue() + "' for field '" + fieldName + "'";

            if (ife.getTargetType() == UUID.class) {
                message = "Invalid UUID format for field '" + fieldName + "'";
            }

            if (ife.getTargetType().isEnum()) {
                String validValues = Arrays.stream(ife.getTargetType().getEnumConstants())
                        .map(Object::toString)
                        .collect(Collectors.joining(", ", "[", "]"));

                message = "Invalid value '" + ife.getValue() + "' for " + fieldName +
                        ". Valid values are: " + validValues;
            }

            loggingService.catchException(ex, message);
            loggingService.logError(ex);
            return ApiResponseUtil.error(HttpStatus.BAD_REQUEST, message);
        }

        message = "Invalid request body";
        loggingService.catchException(ex);
        loggingService.logError(ex);
        return ApiResponseUtil.error(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Object> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        loggingService.catchException(ex);
        loggingService.logError(ex);

        return ApiResponseUtil.error(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<Object> handleSignatureException(SignatureException ex) {
        String message = "Invalid token signature";
        loggingService.catchException(ex);
        loggingService.logError(ex);

        return ApiResponseUtil.error(HttpStatus.UNAUTHORIZED, message);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String message = "Invalid argument type";
        loggingService.catchException(ex);
        loggingService.logError(ex);

        return ApiResponseUtil.error(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<Object> handleMalformedJwtException(MalformedJwtException ex) {
        String message = "Invalid token format";
        loggingService.catchException(ex);
        loggingService.logError(ex);

        return ApiResponseUtil.error(HttpStatus.UNAUTHORIZED, message);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        loggingService.catchException(ex);
        loggingService.logError(ex);

        return ApiResponseUtil.error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<Object> handleMissingRequestHeaderException(MissingRequestHeaderException ex) {
        loggingService.catchException(ex);
        loggingService.logError(ex);

        return ApiResponseUtil.error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException ex) {
        loggingService.catchException(ex);
        loggingService.logError(ex);

        return ApiResponseUtil.error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Object> handleUnauthorizedException(UnauthorizedException ex) {
        loggingService.catchException(ex);
        loggingService.logError(ex);

        return ApiResponseUtil.error(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<Object> handleJsonProcessingException(JsonProcessingException ex) {
        loggingService.catchException(ex);
        loggingService.logError(ex);

        return ApiResponseUtil.error(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Object> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        loggingService.catchException(ex);
        loggingService.logError(ex);

        return ApiResponseUtil.error(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Object> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        log.error(ex.getMessage());
        return ApiResponseUtil.error(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported media type");
    }
}