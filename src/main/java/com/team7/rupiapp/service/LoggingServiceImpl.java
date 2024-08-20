package com.team7.rupiapp.service;

import com.team7.rupiapp.util.LogUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Slf4j
@Service
public class LoggingServiceImpl implements LoggingService {

    @Override
    public <T extends Throwable> void logErrorMessage(T t) {
        log.error(t.getMessage());
    }

    @Override
    public <T extends Throwable> void logErrorMessageWithStackTrace(T t) {
        log.error(t.getMessage(), t);
    }

    @Override
    public <T extends Throwable> void logErrorMessageCustom(T t, String message) {
        log.error(message, t);
    }

    @Override
    public void logTrace(LogUtil.LogHttpRequest log) {
        LoggingServiceImpl.log.trace(log.toJson());

        MDC.put("correlation_id", log.getCorrelationId());
        MDC.put(log.getCorrelationId(), log.toJson());
    }

    @Override
    public void logTrace(LogUtil.LogAuthorizedHttpRequest log) {
        logTrace((LogUtil.LogHttpRequest) log);

        MDC.put("is_authorized", "true");
    }

    @Override
    public void logTrace(LogUtil.LogHttpResponse log) {
        LoggingServiceImpl.log.trace(log.toJson());
    }

    @Override
    public void logTrace(LogUtil.LogAuthorizedHttpResponse log) {
        LoggingServiceImpl.log.trace(log.toJson());
    }

    @Override
    public void logTrace(LogUtil.LogHttpResponseWithException log) {
        LoggingServiceImpl.log.trace(log.toJson());
    }

    @Override
    public void logTrace(LogUtil.LogHttpResponseWithExceptionStackTrace log) {
        LoggingServiceImpl.log.trace(log.toJson());
    }

    @Override
    public void logTrace(LogUtil.LogAuthorizedHttpResponseWithException log) {
        LoggingServiceImpl.log.trace(log.toJson());
    }

    @Override
    public void logTrace(LogUtil.LogAuthorizedHttpResponseWithExceptionStackTrace log) {
        LoggingServiceImpl.log.trace(log.toJson());
    }

    @Override
    public <T extends Exception> void catchException(T e, boolean withStackTrace) {
        catchExceptionDetails(e, e.getMessage(), withStackTrace);
    }

    @Override
    public <T extends Exception> void catchException(T e, boolean withStackTrace, String message) {
        catchExceptionDetails(e, message, withStackTrace);
    }

    private <T extends Exception> void catchExceptionDetails(T e, String message, boolean withStackTrace) {
        MDC.put("is_error", "true");
        MDC.put("exception_message", message);
        if (withStackTrace) {
            MDC.put("is_error_with_stack_trace", "true");
            MDC.put("exception_stack_trace", Arrays.toString(e.getStackTrace()));
        }
    }
}
