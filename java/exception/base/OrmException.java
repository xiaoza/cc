package com.sankuai.meituan.deal.exception.base;

import com.sankuai.meituan.deal.constant.LogLevel;
import org.springframework.jdbc.UncategorizedSQLException;

/**
 * Created by clownfish on 15/2/27.
 */
public class OrmException extends DealException {

    public OrmException() {
    }

    public OrmException(String message) {
        super(message);
    }

    public OrmException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrmException(Throwable cause) {
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
        if (isSerious()) {
            return LogLevel.ERROR;
        }
        return LogLevel.WARN;
    }

    @Override
    public String getShowMessage() {
        if (getCause() instanceof UncategorizedSQLException) {
            return "数据保存失败，请检查是否存在特殊字符";
        }
        return "数据操作异常，请稍后再试";
    }

    private boolean isSerious() {
        if (getCause() instanceof UncategorizedSQLException) {
            return false;
        }
        return true;
    }

}
