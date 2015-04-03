package com.sankuai.meituan.deal.exception;

import com.sankuai.meituan.deal.constant.LogLevel;
import com.sankuai.meituan.deal.exception.base.DealInterruptException;

/**
 * Created by clownfish on 15/3/6.
 */
public class ImageProcessException extends DealInterruptException {
    public ImageProcessException() {
    }

    public ImageProcessException(String message) {
        super(message);
    }

    public ImageProcessException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImageProcessException(Throwable cause) {
        super(cause);
    }

    @Override
    public LogLevel getLogLevel() {
        return LogLevel.WARN;
    }
}
