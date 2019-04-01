/**
 * 文件名：CommonInterceptor.java
 * 
 * 上海迈辰信息科技有限公司(http://www.maxeltech.com)
 * Copyright (c) 2012 MAXEL-TECH Corporation
 */
package com.maxeltech.smcc.interceptor;

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;

/**
 * <p>
 * <li>Description:公共拦截器</li>
 * <li>$Author: chelongquan $</li>
 * <li>$Revision: 71465 $</li>
 * <li>$Date: 2017-12-07 17:07:01 +0800 (Thu, 07 Dec 2017) $</li>
 * 
 * @version 1.0
 */
public class CommonInterceptor implements HandlerInterceptor {

    /**
     * 日志对象
     */
    protected final Logger log = Logger.getLogger(getClass());

    private final ThreadLocal<Long> timeLocal = new ThreadLocal<Long>();

    @Override
    public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
            throws Exception {
        // if (responseDto != null) {
        // Object resultData = responseDto.getData();
        // if (resultData != null && resultData instanceof Security) {
        // ((Security) resultData).encrypt();
        // } else if (resultData instanceof List && !((List<?>) resultData).isEmpty()) {
        // List<?> list = (List<?>) resultData;
        // if (list.get(0) instanceof Security) {
        // for (Object objList : list) {
        // ((Security) objList).encrypt();
        // }
        // }
        // } else if (resultData instanceof Set && !((Set<?>) resultData).isEmpty()) {
        // Set<?> set = (Set<?>) resultData;
        // if (set.iterator().next() instanceof Security) {
        // for (Object objList : set) {
        // ((Security) objList).encrypt();
        // }
        // }
        // } else if (resultData instanceof PageDto) {
        // List<?> list = ((PageDto<?>) resultData).getData();
        // if (CollectionUtils.isNotEmpty(list)) {
        // if (list.get(0) instanceof Security) {
        // for (Object objList : list) {
        // ((Security) objList).encrypt();
        // }
        // }
        // }
        // }
        // }
        if (log.isDebugEnabled()) {
            log.debug(arg0.getServletPath() + "客户端请求总耗时:" + (System.currentTimeMillis() - timeLocal.get()) + "ms");
        }
        timeLocal.remove();
    }

    @Override
    public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
            throws Exception {
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object arg2) throws Exception {
        timeLocal.remove();
        // 提取请求参数
        Map<String, Object> urlDecodedParamsMap = Maps.newHashMap();
        if (null != request.getParameterMap() && !request.getParameterMap().isEmpty()) {
            Set<?> paramNameSet = request.getParameterMap().keySet();
            for (Object paramName : paramNameSet) {
                String paramValue = request.getParameter(paramName.toString());
                // paramValue = URLDecoder.decode(paramValue, "utf-8");
                try {
                    Object o = JSONObject.parse(paramValue);
                    urlDecodedParamsMap.put(paramName.toString(), o);
                } catch (Exception e) {
                    urlDecodedParamsMap.put(paramName.toString(), paramValue);
                }
            }
        }
        // 将param中的参数提取出来放到request中
        for (String key : urlDecodedParamsMap.keySet()) {
            request.setAttribute(key, urlDecodedParamsMap.get(key));
        }
        timeLocal.set(System.currentTimeMillis());
        return true;
    }
}
