package com.sankuai.meituan.deal.exception.http;

import com.sankuai.meituan.common.rpc.InvokeException;
import com.sankuai.meituan.deal.exception.base.DealHttpException;

/**
 * Created by clownfish on 15/2/26.
 */
public class HttpMtopenException extends DealHttpException {

    public HttpMtopenException(String message, InvokeException cause) {
        super(message, cause);
    }

    public HttpMtopenException(InvokeException cause) {
        super(cause);
    }

    @Override
    protected String getName() {
        return "MtOpen";
    }
}
