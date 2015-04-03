package com.sankuai.meituan.deal.exception;

import com.sankuai.meituan.deal.exception.base.ValidatorException;

/**
 * Created by wangpeng on 15/3/17.
 */
public class ValidateTokenException extends ValidatorException {
    public ValidateTokenException() {

    }

    public ValidateTokenException(String message) {
        super(message);
    }

    public ValidateTokenException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public ValidateTokenException(Throwable throwable) {
        super(throwable);
    }
}
