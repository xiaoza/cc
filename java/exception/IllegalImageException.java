package com.sankuai.meituan.deal.exception;

import com.sankuai.meituan.deal.exception.base.ValidatorException;

/**
 * Created by clownfish on 15/3/17.
 */
public class IllegalImageException extends ValidatorException {
    public IllegalImageException() {
    }

    public IllegalImageException(String message) {
        super(message);
    }

    public IllegalImageException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalImageException(Throwable cause) {
        super(cause);
    }
}
