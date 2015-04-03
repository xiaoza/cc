package com.sankuai.meituan.deal.exception.base;

import com.sankuai.meituan.common.rpc.InvokeException;
import com.sankuai.meituan.deal.constant.LogLevel;
import com.sankuai.meituan.deal.util.StringUtil;

/**
 * Created by clownfish on 15/2/26.
 */
public class DealHttpException extends DealException{

    private static final String DEFAULT_MESSAGE = "接口调用失败";

    private InvokeException ie;

    public DealHttpException(String message, InvokeException cause) {
        super(message, cause);
        ie = cause;
    }

    public DealHttpException(InvokeException cause) {
        this(cause.getMessage(), cause);
        ie = cause;
    }

    @Override
    public int getCode() {
        return ie.getCode();
    }

    @Override
    public String getType() {
        return ie.getType();
    }

    @Override
    public LogLevel getLogLevel() {
        if (getCode() == 500) {
            return LogLevel.ERROR;
        }
        return LogLevel.WARN;
    }

    @Override
    public String getMessage() {
        if (StringUtil.isNotBlank(super.getMessage())) {
            return super.getMessage();
        }
        return getName() + DEFAULT_MESSAGE;
    }

    protected String getName() {
        return "http";
    }
}
