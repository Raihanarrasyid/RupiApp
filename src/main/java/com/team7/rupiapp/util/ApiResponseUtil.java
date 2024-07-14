package com.team7.rupiapp.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.team7.rupiapp.dto.StandardResponseModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiResponseUtil {

    private ApiResponseUtil() {
    }

    private static ResponseEntity<Object> buildResponse(HttpStatus status, String message, Object data,
            String requestID) {
        Map<String, Object> response = new HashMap<>();
        if (requestID != null) {
            response.put("request_id", requestID);
        }
        response.put("message", message);
        if (data != null) {
            if (status.is2xxSuccessful()) {
                response.put("data", data);
            } else {
                response.put("errors", data);
            }
        }

        return ResponseEntity.status(status).body(response);
    }

    public static ResponseEntity<Object> success(HttpStatus status, String message) {
        return buildResponse(status, message, null, null);
    }

    public static ResponseEntity<Object> success(HttpStatus status, String message, Object data) {
        return buildResponse(status, message, data, null);
    }

    public static ResponseEntity<Object> error(HttpStatus status, String message) {
        return buildResponse(status, message, null, null);
    }

    public static ResponseEntity<Object> error(HttpStatus status, String message, Object details) {
        return buildResponse(status, message, details, null);
    }

    public static ResponseEntity<Object> error(HttpStatus status, String message, String requestID) {
        return buildResponse(status, message, null, requestID);
    }

    public static <T> ResponseEntity<StandardResponseModel<T>> createSuccessResponse(String message, HttpStatus status) {
        StandardResponseModel<T> response = StandardResponseModel.<T>builder()
                .success(true)
                .message(message)
                .build();
        return new ResponseEntity<>(response, status);
    }

    public static <T> ResponseEntity<StandardResponseModel<T>> createSuccessResponseWithData(String message, T data, HttpStatus status) {
        StandardResponseModel<T> response = StandardResponseModel.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
        return new ResponseEntity<>(response, status);
    }

    public static ResponseEntity<StandardResponseModel<Void>> createErrorResponse(String message, List<Map<String,
            String>> errors, HttpStatus status) {
        StandardResponseModel<Void> response = StandardResponseModel.<Void>builder()
                .success(false)
                .message(message)
                .errors(errors)
                .build();
        return new ResponseEntity<>(response, status);
    }
}
