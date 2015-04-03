package com.sankuai.meituan.deal.exception.base;

import com.sankuai.meituan.deal.constant.LogLevel;

/**
 * Created by clownfish on 15/3/3.
 */
public class DealInterruptException extends DealException{

    public DealInterruptException() {
    }

    public DealInterruptException(String message) {
        super(message);
    }

    public DealInterruptException(String message, Throwable cause) {
        super(message, cause);
    }

    public DealInterruptException(Throwable cause) {
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
