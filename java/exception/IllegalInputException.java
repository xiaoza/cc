package com.sankuai.meituan.deal.exception;

import com.sankuai.meituan.deal.exception.base.ValidatorException;

/**
 * Created by clownfish on 15/2/26.
 */
public class IllegalInputException extends ValidatorException {

    public IllegalInputException() {
    }

    public IllegalInputException(String message) {
        super(message);
    }

    public IllegalInputException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalInputException(Throwable cause) {
        super(cause);
    }

}
