package com.sankuai.meituan.deal.exception.base;

import com.sankuai.meituan.deal.constant.LogLevel;

/**
 * Created by clownfish on 15/2/27.
 */
public class DealBusinessException extends DealException {

    public DealBusinessException() {
    }

    public DealBusinessException(String message) {
        super(message);
    }

    public DealBusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public DealBusinessException(Throwable cause) {
        super(cause);
    }

    @Override
    public int getCode() {
        return DealError.BUSINESS.getCode();
    }

    @Override
    public String getType() {
        return DealError.BUSINESS.getType();
    }

    @Override
    public LogLevel getLogLevel() {
        return LogLevel.WARN;
    }
}
