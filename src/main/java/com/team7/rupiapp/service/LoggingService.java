package com.team7.rupiapp.service;

import com.team7.rupiapp.util.LogUtil;

public interface LoggingService {
    public <T extends Throwable> void logErrorMessageWithStackTrace(T t);
    public <T extends Throwable> void logErrorMessage(T t);
    public <T extends Throwable> void logErrorMessageCustom(T t, String message);
    public void logTrace(LogUtil.LogHttpRequest logHttpRequest);
    public void logTrace(LogUtil.LogHttpResponseWithExceptionStackTrace log);
    public void logTrace(LogUtil.LogAuthorizedHttpRequest logAuthorizedHttpRequest);
    public void logTrace(LogUtil.LogAuthorizedHttpResponseWithExceptionStackTrace log);
    public <T extends Exception> void catchException(T e, boolean withStackTrace);
    public <T extends Exception> void catchException(T e, boolean withStackTrace, String message);
    public void logTrace(LogUtil.LogHttpResponse log);
    public void logTrace(LogUtil.LogAuthorizedHttpResponse log);
    public void logTrace(LogUtil.LogHttpResponseWithException log);
    public void logTrace(LogUtil.LogAuthorizedHttpResponseWithException log);
}
