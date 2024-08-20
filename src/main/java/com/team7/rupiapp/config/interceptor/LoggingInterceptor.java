package com.team7.rupiapp.config.interceptor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.team7.rupiapp.service.LoggingService;
import com.team7.rupiapp.util.LogUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private final LoggingService loggingService;

    public LoggingInterceptor(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String mdcCorrelationId = MDC.get("correlation_id");
        String mdcLog = MDC.get(mdcCorrelationId);
        boolean isError = Boolean.parseBoolean((MDC.get("is_error")));
        boolean isAuthorized = Boolean.parseBoolean((MDC.get("is_authorized")));

        try {
            if (isError) {
                String exceptionMessage = MDC.get("exception_message");
                String exceptionStackTrace = MDC.get("exception_stack_trace");

                if (!isAuthorized) {
                    LogUtil.LogHttpResponseWithException log =
                            LogUtil.readLogHttpResponseWithException(mdcLog,
                                    LogUtil.LogHttpResponseWithException.class);
                    log.setHttpStatus(HttpStatus.valueOf(response.getStatus()));
                    log.setHttpStatusCode(response.getStatus());
                    log.setExceptionMessage(exceptionMessage);
                    log.setExceptionStackTrace(exceptionStackTrace);
                    loggingService.logTrace(log);
                } else {
                    LogUtil.LogAuthorizedHttpResponseWithException log = LogUtil.readLogAuthorizedHttpResponseWithException(mdcLog,
                            LogUtil.LogAuthorizedHttpResponseWithException.class);
                    log.setHttpStatus(HttpStatus.valueOf(response.getStatus()));
                    log.setHttpStatusCode(response.getStatus());
                    log.setExceptionMessage(exceptionMessage);
                    log.setExceptionStackTrace(exceptionStackTrace);
                    loggingService.logTrace(log);
                }
            } else {
                if (!isAuthorized) {
                    LogUtil.LogHttpResponse log = LogUtil.readLogHttpResponse(mdcLog, LogUtil.LogHttpResponse.class);
                    log.setHttpStatus(HttpStatus.valueOf(response.getStatus()));
                    log.setHttpStatusCode(response.getStatus());
                    loggingService.logTrace(log);
                } else {
                    LogUtil.LogAuthorizedHttpResponse log = LogUtil.readLogAuthorizedHttpResponse(mdcLog, LogUtil.LogAuthorizedHttpResponse.class);
                    log.setHttpStatus(HttpStatus.valueOf(response.getStatus()));
                    log.setHttpStatusCode(response.getStatus());
                    loggingService.logTrace(log);
                }
            }

            MDC.clear();
        } catch (JsonProcessingException e) {
            log.error("[" + this.getClass().getSimpleName() + "] Error parsing log: {}", e.getMessage());
            throw new RuntimeException(e);
        } catch (Exception e) {
            log.error("[" + this.getClass().getSimpleName() + "] Error unknown : {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}