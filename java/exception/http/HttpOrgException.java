package com.sankuai.meituan.deal.exception.http;

import com.sankuai.meituan.common.rpc.InvokeException;
import com.sankuai.meituan.deal.exception.base.DealHttpException;

/**
 * Created by clownfish on 15/2/25.
 */
public class HttpOrgException extends DealHttpException {

    public HttpOrgException(String message, InvokeException cause) {
        super(message, cause);
    }

    public HttpOrgException(InvokeException cause) {
        super(cause);
    }

    @Override
    protected String getName() {
        return "Org";
    }
}
