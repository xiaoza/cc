package com.sankuai.meituan.deal.exception;

import com.sankuai.meituan.deal.exception.base.ValidatorException;

/**
 * Created by clownfish on 15/2/26.
 */
public class IllegalParameterException extends ValidatorException {

    public IllegalParameterException() {
    }

    public IllegalParameterException(String message) {
        super(message);
    }

    public IllegalParameterException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalParameterException(Throwable cause) {
        super(cause);
    }

}
