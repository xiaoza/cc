package com.sankuai.meituan.deal.exception;

import com.sankuai.meituan.deal.exception.base.DealBusinessException;

/**
 * Created by clownfish on 15/2/28.
 */
public class NotFoundException extends DealBusinessException {
    public NotFoundException() {
    }

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundException(Throwable cause) {
        super(cause);
    }

    @Override
    public int getCode() {
        return DealError.EMPTY.getCode();
    }

    @Override
    public String getType() {
        return DealError.EMPTY.getType();
    }
}
