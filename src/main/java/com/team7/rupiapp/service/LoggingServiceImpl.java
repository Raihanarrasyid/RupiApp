package com.team7.rupiapp.service;

import com.team7.rupiapp.util.LogUtil;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LoggingServiceImpl implements LoggingService {

    public void logTrace(LogUtil.LogHttpRequest logHttpRequest, HttpServletRequest request) {
        log.trace(logHttpRequest.toJson());
    }

    public void logTrace(LogUtil.Log log) {
        LoggingServiceImpl.log.trace(log.toJson());
    }

    @Override
    public void logDebug(String message) {
        log.debug(message);
    }

    @Override
    public void logInfo(String message) {
        log.info(message);
    }

    public void logInfo(LogUtil.LogHttpRequest logHttpRequest) {
        log.info(logHttpRequest.toJson());
    }

    @Override
    public void logWarn(String message) {
        log.warn(message);
    }

    @Override
    public <T extends Throwable> void logError(T t) {
        log.error(t.getMessage(), t);
    }

    @Override
    public <T extends Throwable> void logError(T t, String message) {
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
    public void logTrace(LogUtil.LogAuthorizedHttpResponseWithException log) {
        LoggingServiceImpl.log.trace(log.toJson());
    }

    @Override
    public <T extends Exception> void catchException(T e) {
        catchExceptionDetails(e, e.getMessage());
    }

    @Override
    public <T extends Exception> void catchException(T e, String message) {
        catchExceptionDetails(e, message);
    }

    private <T extends Exception> void catchExceptionDetails(T e, String message) {
        MDC.put("is_error", "true");
        MDC.put("exception_message", message);
        MDC.put("exception_stack_trace", Arrays.toString(e.getStackTrace()));
    }
}
