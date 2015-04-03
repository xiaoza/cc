package com.sankuai.meituan.deal.exception.http;

import com.sankuai.meituan.common.rpc.InvokeException;
import com.sankuai.meituan.deal.exception.base.DealHttpException;

/**
 * Created by clownfish on 15/2/12.
 */
public class HttpPoiopException extends DealHttpException{

    public HttpPoiopException(String message, InvokeException cause) {
        super(message, cause);
    }

    public HttpPoiopException(InvokeException cause) {
        super(cause);
    }

    @Override
    protected String getName() {
        return "Poiop";
    }
}
