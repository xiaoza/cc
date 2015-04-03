package com.sankuai.meituan.deal.exception;

import com.sankuai.meituan.deal.exception.base.ViewException;
import freemarker.core.Environment;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Writer;

/**
 * Created by clownfish on 15/3/6.
 */
public class FreemarkerExceptionHandler implements TemplateExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(FreemarkerExceptionHandler.class);

    @Override
    public void handleTemplateException(TemplateException te, Environment env, Writer out) throws TemplateException {
        throw new ViewException("freemarker error,"+te.getMessage(), te);
    }

}
