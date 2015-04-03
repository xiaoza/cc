package com.sankuai.meituan.deal.exception;

import com.sankuai.meituan.deal.exception.base.DealInterruptException;

/**
 * Created by clownfish on 15/3/6.
 */
public class ShouldNotNullException extends DealInterruptException {

    public ShouldNotNullException() {
    }

    public ShouldNotNullException(String message) {
        super(message);
    }

    public ShouldNotNullException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShouldNotNullException(Throwable cause) {
        super(cause);
    }
}
