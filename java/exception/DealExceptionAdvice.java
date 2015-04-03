package com.sankuai.meituan.deal.exception;

import com.sankuai.meituan.common.rpc.InvokeException;
import com.sankuai.meituan.deal.exception.base.DealException;
import com.sankuai.meituan.deal.exception.base.DealHttpException;
import com.sankuai.meituan.deal.exception.base.OrmException;
import com.sankuai.meituan.deal.exception.http.*;
import com.sankuai.meituan.deal.util.ObjectUtil;
import com.sankuai.meituan.deal.util.StringUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.dao.DataAccessException;

import java.lang.reflect.Method;

/**
 * 对 Invoke 和 mybatis 异常进行拦截，重新包装后抛出
 * Created by yuzhen on 15/2/12.
 */
@Aspect
public class DealExceptionAdvice {

    @Pointcut("execution(* com.sankuai.meituan.deal..*.*(..))")
    private void allDealMethods(){}

    @AfterThrowing(pointcut = "allDealMethods()", throwing = "ex")
    public void afterThrowing(JoinPoint joinPoint, Exception ex) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();

        if (ex instanceof DealException) {
            // 获取抛出异常的方法名称和参数
            ((DealException)ex).setMethodAndParams(getMethodParams(method, args));
        } else if (ex instanceof InvokeException) {
            throwHttpException(method, args, (InvokeException)ex);
        } else if (isOrmError(ex)) {
            throwOrmException(method, args, ex);
        }
    }

    private void throwHttpException(String host, Method method, Object[] args, InvokeException ex) {
        if (StringUtil.isBlank(host)) {
            return;
        }

        if ("mtbase".equals(host)) {
            DealHttpException exception = new HttpMisException(ex);
            exception.setMethodAndParams(getMethodParams(method, args));
            throw exception;
        }
//        switch (host) {
//            case "mtct":
//                exception = new HttpCtException(ex);
//                break;
//            case "mtcms_api":
//                exception = new HttpCmsException(ex);
//                break;
//            case "mtcrm":
//                exception = new HttpCrmException(ex);
//                break;
//            case "mtorg":
//                exception = new HttpOrgException(ex);
//                break;
//            case "mtprofit":
//                exception = new HttpProfitException(ex);
//                break;
//            case "mtpoiop":
//                exception = new HttpPoiopException(ex);
//                break;
//            case "mtgis":
//                exception = new HttpGisException(ex);
//                break;
//            case "mtopenin":
//                exception = new HttpMainWebException(ex);
//                break;
//            case "mtmis":
//                exception = new HttpMisException(ex);
//                break;
//            case "mtbase":
//                exception = new HttpMisException(ex);
//                break;
//            default:
//                exception = new DealHttpException(ex);
//        }
//        throw exception;
    }

    private void throwHttpException(Method method, Object[] args, InvokeException ex) {
        DealHttpException http = new DealHttpException(ex);
        http.setMethodAndParams(getMethodParams(method, args));
        throw http;
    }

    private void throwOrmException(Method method, Object[] args, Exception ex) {
        OrmException orm = new OrmException(ex);
        orm.setMethodAndParams(getMethodParams(method, args));
        throw orm;
    }

    private String getMethodParams(Method method, Object[] params) {
        StringBuilder builder = new StringBuilder(512);
        builder.append("method=").append(method.getName()).append(",");
        if (params != null && params.length > 0) {
            builder.append("params=");
            for (Object param : params) {
                if (param == null) {
                    builder.append("null").append(";");
                } else {
                    builder.append(ObjectUtil.toString(param)).append(";");
                }
            }
        }
        return builder.toString();
    }

    private boolean isOrmError(Exception ex) {
        if (ex instanceof MyBatisSystemException) {
            return true;
        }
        if (ex instanceof DataAccessException) {
            return true;
        }
        return false;
    }
}
