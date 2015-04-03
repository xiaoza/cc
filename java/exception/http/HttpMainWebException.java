package com.sankuai.meituan.deal.exception.http;

import com.sankuai.meituan.common.rpc.InvokeException;
import com.sankuai.meituan.deal.constant.LogLevel;
import com.sankuai.meituan.deal.exception.base.DealHttpException;

/**
 * Created by clownfish on 15/2/12.
 */
public class HttpMainWebException extends DealHttpException {

    public HttpMainWebException(String message, InvokeException cause) {
        super(message, cause);
    }

    public HttpMainWebException(InvokeException cause) {
        super(cause);
    }

    @Override
    protected String getName() {
        return "MainWeb";
    }

    @Override
    public LogLevel getLogLevel() {
        return super.getLogLevel();
    }
}
