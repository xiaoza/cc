package com.sankuai.meituan.deal.exception.base;

import com.sankuai.meituan.deal.constant.LogLevel;

/**
 * Created by clownfish on 15/3/6.
 */
public class ViewException extends DealException {
    public ViewException() {
    }

    public ViewException(String message) {
        super(message);
    }

    public ViewException(String message, Throwable cause) {
        super(message, cause);
    }

    public ViewException(Throwable cause) {
        super(cause);
    }

    @Override
    public int getCode() {
        return DealError.INTERNAL.getCode();
    }

    @Override
    public String getType() {
        return DealError.INTERNAL.getType();
    }

    @Override
    public LogLevel getLogLevel() {
        return LogLevel.WARN;
    }
}
