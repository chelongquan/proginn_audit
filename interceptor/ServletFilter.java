/**
 * 文件名：ServletFilter.java
 * 
 * 上海迈辰信息科技有限公司(http://www.maxeltech.com)
 * Copyright (c) 2014 MAXEL-TECH Corporation
 */
package com.maxeltech.smcc.interceptor;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.maxeltech.smcc.plugins.jdbc.core.DataSourceInterceptor;

/**
 * <p>
 * <li>Description:Servlet过滤器</li>
 * <li>$Author: luwangjin $</li>
 * <li>$Revision: 3947 $</li>
 * <li>$Date: 2018-08-22 09:04:56 +0800 (Wed, 22 Aug 2018) $</li>
 * 
 * @version 1.0
 */
public class ServletFilter implements Filter {

    /**
     * 销毁
     */
    @Override
    public void destroy() {
    }

    /**
     * 过滤
     * 
     * @param request 请求
     * @param response 响应
     * @param chain 过滤链
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        DataSourceInterceptor.reset(null);
        chain.doFilter(request, response);
    }

    /**
     * 初始化
     */
    @Override
    public void init(FilterConfig arg0) throws ServletException {
    }
}
