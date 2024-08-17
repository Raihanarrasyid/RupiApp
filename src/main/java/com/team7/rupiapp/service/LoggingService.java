package com.team7.rupiapp.service;

import com.team7.rupiapp.util.LogUtil;

public interface LoggingService {
    public void logDebug(String message);
    public void logInfo(String message);
    public void logInfo(LogUtil.LogHttpRequest logHttpRequest);
    public void logWarn(String message);
    public void logError(String message, Throwable t);
    public void logTrace(LogUtil.LogHttpRequest logHttpRequest);
    public void logTrace(LogUtil.LogAuthorizedHttpRequest logAuthorizedHttpRequest);
    public <T extends Exception> void catchException(T e);
    public <T extends Exception> void catchException(T e, String message);
    public void logTrace(LogUtil.LogHttpResponse log);
    public void logTrace(LogUtil.LogAuthorizedHttpResponse log);
    public void logTrace(LogUtil.LogHttpResponseWithException log);
    public void logTrace(LogUtil.LogAuthorizedHttpResponseWithException log);
}
