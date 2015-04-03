package com.sankuai.meituan.deal.exception.base;

import com.sankuai.meituan.deal.constant.LogLevel;
import com.sankuai.meituan.deal.util.StringUtil;

/**
 * Created by clownfish on 15/2/26.
 */
public abstract class DealException extends RuntimeException {

    private int code;
    private String type;
    private LogLevel logLevel;

    private String methodAndParams;
    private String showMessage;

    public DealException() {
    }

    public DealException(String message) {
        super(message);
    }

    public DealException(String message, Throwable cause) {
        super(message, cause);
    }

    public DealException(Throwable cause) {
        super(cause);
    }

    public abstract int getCode();

    public abstract String getType();

    public abstract LogLevel getLogLevel();

    /**
     * 给客户端看的message
     */
    public String getShowMessage() {
        if (StringUtil.isBlank(showMessage)) {
            return getMessage();
        }
        return showMessage;
    }

    public void setShowMessage(String showMessage) {
        this.showMessage = showMessage;
    }

    public String getMethodAndParams() {
        return methodAndParams;
    }

    public void setMethodAndParams(String methodAndParams) {
        this.methodAndParams = methodAndParams;
    }

    protected enum DealError {
        AUTH(401, "Unauthorized"),
        BUSINESS(400, "Business Error"),
        ILLEGAL(403, "Access Forbidden"),
        EMPTY(404, "Not Found"),
        INTERNAL(500, "Internal Server Error");

        private int code;
        private String type;

        DealError(int code, String type) {
            this.code = code;
            this.type = type;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
