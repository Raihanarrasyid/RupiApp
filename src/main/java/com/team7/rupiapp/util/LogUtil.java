package com.team7.rupiapp.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import jakarta.servlet.http.HttpServletRequest;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.UUID;

public class LogUtil {

    public static final ObjectMapper objectMapper =
            new ObjectMapper()
                    .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

    private static String toJson(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static LogAuthorizedHttpResponseWithException readLogAuthorizedHttpResponseWithException(String mdcLog, Class<LogAuthorizedHttpResponseWithException> log) throws JsonProcessingException {
        return objectMapper.readValue(mdcLog, log);
    }

    public static LogHttpResponseWithException readLogHttpResponseWithException(String mdcLog, Class<LogHttpResponseWithException> log) throws JsonProcessingException {
        return objectMapper.readValue(mdcLog, log);
    }

    public static LogAuthorizedHttpResponse readLogAuthorizedHttpResponse(String mdcLog, Class<LogAuthorizedHttpResponse> log) throws JsonProcessingException {
        return objectMapper.readValue(mdcLog, log);
    }

    public static LogHttpResponse readLogHttpResponse(String mdcLog, Class<LogHttpResponse> log) throws JsonProcessingException {
        return objectMapper.readValue(mdcLog, log);
    }

    public enum Type {
        REQUEST,
        RESPONSE
    }

    @Getter
    @AllArgsConstructor
    public enum Service {
        AUTH_SERVICE("Auth Service", "auth"),
        ACCOUNT_SERVICE("Account Service", "account"),
        TRANSFER_SERVICE("Transfer Service", "transfer"),
        USER_SERVICE("User Service", "users"); // Add more services as needed

        private final String name;
        private final String serviceId;

        public static Service getServiceName(String uri) {
            if (uri.contains(ACCOUNT_SERVICE.serviceId)) {
                return ACCOUNT_SERVICE;
            } else if (uri.contains(TRANSFER_SERVICE.serviceId)) {
                return TRANSFER_SERVICE;
            } else if (uri.contains(USER_SERVICE.serviceId)) {
                return USER_SERVICE;
            } else if (uri.contains(AUTH_SERVICE.serviceId)) {
                return AUTH_SERVICE;
            } else {
                return null;
            }
        }
    }

    /**
     * <p>Logging format:<br>
     * {@code {"service":"VALUE","correlation_id":"VALUE"}}</p>
     * <p>Example:<br>
     * {@code {"service":"Auth Service","correlation_id":"123e4567-e89b-12d3-a456-426614174000"}}</p>
     */
    @Data
    @NoArgsConstructor
    public static class Log {
        private Service service;
        private UUID correlationId = UUID.randomUUID();

        public Log(Service service) {
            this.service = service;
        }

        public String getCorrelationId() {
            return correlationId.toString();
        }

        public String toJson() {
            return LogUtil.toJson(this);
        }
    }

    /**
     * <p>Logging format:<br>
     * {@code {"service":"VALUE","correlation_id":"VALUE","type":"REQUEST","request_method":"VALUE","uri":"VALUE"}}</p>
     * <p>Example:<br>
     * {@code {"service":"Auth Service","correlation_id":"123e4567-e89b-12d3-a456-426614174000",
     * "type":"REQUEST","request_method":"GET","uri":"/api/v1/account"}}</p>
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    @NoArgsConstructor
    public static class LogHttpRequest extends Log {
        private Type type = Type.REQUEST;
        private RequestMethod requestMethod;
        private String uri;

        public LogHttpRequest(Service service, RequestMethod requestMethod, String uri) {
            super(service);
            this.requestMethod = requestMethod;
            this.uri = uri;
        }

        public LogHttpRequest(HttpServletRequest request) {
            super(Service.getServiceName(request.getRequestURI()));
            this.requestMethod = RequestMethod.valueOf(request.getMethod());
            this.uri = request.getRequestURI();
        }
    }

    /**
     * <p>Logging format:<br>
     * {@code {"service":"VALUE","correlation_id":"VALUE","type":"REQUEST","request_method":"VALUE","uri":"VALUE",
     * "user_id:"VALUE"}}</p>
     * <p>Example:<br>
     * {@code {"service":"Auth Service","correlation_id":"123e4567-e89b-12d3-a456-426614174000","type":"REQUEST",
     * "request_method":"GET","uri":"/api/v1/account","user_id":"65ae7861-c4d1-429b-abd1-771f4e20e45e"}}</p>
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    @NoArgsConstructor
    public static class LogAuthorizedHttpRequest extends LogHttpRequest {
        private UUID userId;

        public LogAuthorizedHttpRequest(LogHttpRequest logHttpRequest, UUID userId) {
            super(logHttpRequest.getService(), logHttpRequest.getRequestMethod(), logHttpRequest.getUri());
            this.userId = userId;
        }

        public LogAuthorizedHttpRequest(Service service, RequestMethod requestMethod, String uri, UUID userId) {
            super(service, requestMethod, uri);
            this.userId = userId;
        }

        public LogAuthorizedHttpRequest(HttpServletRequest request, UUID userId) {
            super(Service.getServiceName(request.getRequestURI()), RequestMethod.valueOf(request.getMethod()), request.getRequestURI());
            this.userId = userId;
        }
    }

    /**
     * <p>Logging format:<br>
     * {@code {"service":"VALUE","correlation_id":"VALUE","type":"RESPONSE","request_method":"VALUE","uri":"VALUE",
     * "http_status":"VALUE","http_status_code":VALUE}}</p>
     * <p>Example:<br>
     * {@code {"service":"Auth Service","correlation_id":"123e4567-e89b-12d3-a456-426614174000",
     * "type":"RESPONSE","request_method":"GET","uri":"/api/v1/account","http_status":"OK","http_status_code":200}}</p>
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    @NoArgsConstructor
    public static class LogHttpResponse extends LogHttpRequest {
        private Type type = Type.RESPONSE;
        private HttpStatus httpStatus;
        private Integer httpStatusCode;

        public LogHttpResponse(LogHttpRequest logHttpRequest, HttpStatus httpStatus) {
            super(logHttpRequest.getService(), logHttpRequest.getRequestMethod(), logHttpRequest.getUri());
            this.httpStatus = httpStatus;
            this.httpStatusCode = httpStatus.value();
        }

        @Override
        public void setType(Type type) {
            this.type = Type.RESPONSE;
        }
    }

    /**
     * <p>Logging format:<br>
     * {@code {"service":"VALUE","correlation_id":"VALUE","type":"RESPONSE","request_method":"VALUE","uri":"VALUE",
     * "user_id:"VALUE","http_status":"VALUE","httpStatusCode":VALUE}}</p>
     * <p>Example:<br>
     * {@code {"service":"Auth Service","correlation_id":"123e4567-e89b-12d3-a456-426614174000",
     * "type":"RESPONSE","request_method":"GET","uri":"/api/v1/account",
     * "user_id":"65ae7861-c4d1-429b-abd1-771f4e20e45e",
     * "http_status":"UNAUTHORIZED","httpStatusCode":401}}</p>
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    @NoArgsConstructor
    public static class LogAuthorizedHttpResponse extends LogAuthorizedHttpRequest {
        private Type type = Type.RESPONSE;
        private HttpStatus httpStatus;
        private Integer httpStatusCode;

        public LogAuthorizedHttpResponse(LogAuthorizedHttpRequest logAuthorizedHttpRequest, HttpStatus httpStatus) {
            super(logAuthorizedHttpRequest.getService(), logAuthorizedHttpRequest.getRequestMethod(), logAuthorizedHttpRequest.getUri(), logAuthorizedHttpRequest.getUserId());
            this.httpStatus = httpStatus;
            this.httpStatusCode = httpStatus.value();
        }

        @Override
        public void setType(Type type) {
            this.type = Type.RESPONSE;
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @NoArgsConstructor
    public static class LogHttpResponseWithException extends LogHttpResponse {
        private String exceptionMessage;
        private String exceptionStackTrace;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @NoArgsConstructor
    public static class LogAuthorizedHttpResponseWithException extends LogAuthorizedHttpResponse {
        private String exceptionMessage;
        private String exceptionStackTrace;
    }
}
