package com.sankuai.meituan.deal.exception.base;

import com.sankuai.meituan.deal.constant.LogLevel;

/**
 * 验证异常.
 * 用于给前端显示验证结果
 *
 * @author yechunyuan
 */
public class ValidatorException extends DealException {

    public ValidatorException() {
    }

    public ValidatorException(String message) {
        super(message);
    }

    public ValidatorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidatorException(Throwable cause) {
        super(cause);
    }

    @Override
    public int getCode() {
        return DealError.ILLEGAL.getCode();
    }

    @Override
    public String getType() {
        return DealError.ILLEGAL.getType();
    }

    @Override
    public LogLevel getLogLevel() {
        return LogLevel.WARN;
    }
}
