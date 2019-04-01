/**
 * 文件名：ExceptionHandler.java
 * 
 * 上海迈辰信息科技有限公司(http://www.maxeltech.com)
 * Copyright (c) 2012 MAXEL-TECH Corporation
 */
package com.maxeltech.smcc.interceptor;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.maxeltech.smcc.exception.BusinessException;
import com.maxeltech.smcc.utils.common.RtMessage;

/**
 * <p>
 * <li>Description:异常处理</li>
 * <li>$Author: chelongquan $</li>
 * <li>$Revision: 68025 $</li>
 * <li>$Date: 2017-05-31 14:32:04 +0800 (Wed, 31 May 2017) $</li>
 * 
 * @version 1.0
 */
@Component
public class ExceptionHandler implements HandlerExceptionResolver {

    private final Logger logger = Logger.getLogger(ExceptionHandler.class);

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex) {
        Throwable e = ex;
        // 拦截器中invocation抛出的异常
        if (ex instanceof UndeclaredThrowableException) {
            e = ((UndeclaredThrowableException) ex).getUndeclaredThrowable();
        }
        BusinessException bException = null;
        if (e instanceof BusinessException) {
            bException = (BusinessException) e;
        } else {
            // 默认未知异常
            bException = new BusinessException();
        }
        try {
            RtMessage.responseBusinessJson(response, bException);
        } catch (IOException e1) {
            e1.printStackTrace();
            logger.error("响应异常", e);
        }
        return null;
    }
}
