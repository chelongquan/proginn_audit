/**
 * 文件名：CustomArgumentResolvers.java
 * 
 * 上海迈辰信息科技有限公司(http://www.maxeltech.com)
 * Copyright (c) 2012 MAXEL-TECH Corporation
 */
package com.maxeltech.smcc.interceptor;

import java.lang.reflect.ParameterizedType;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.maxeltech.smcc.exception.BusinessException;
import com.maxeltech.smcc.utils.common.DateTool;
import com.maxeltech.smcc.utils.common.StringUtils;
//import net.sf.ezmorph.object.DateMorpher;
//import net.sf.json.JSONObject;
//import net.sf.json.util.JSONUtils;

/**
 * <p>
 * <li>Description:params绑定参数,自定义检查</li>
 * <li>$Author: chelongquan $</li>
 * <li>$Revision: 69255 $</li>
 * <li>$Date: 2017-08-01 17:56:58 +0800 (Tue, 01 Aug 2017) $</li>
 * 
 * @version 1.0
 */
public class CustomParamResolver implements HandlerMethodArgumentResolver {

    private final Logger logger = Logger.getLogger(CustomParamResolver.class);

    private static String[] dateFormates = new String[] { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd  HH:mm" };

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return true;
    }

    /**
     * 自定义参数解析
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Object requestObject = webRequest.getNativeRequest();
        if (!(requestObject instanceof HttpServletRequest)) {
            throw new BusinessException();
        }
        try {
            Class cls = methodParameter.getParameterType();
            HttpServletRequest request = (HttpServletRequest) requestObject;
            String name = methodParameter.getParameterName();
            Object value = request.getAttribute(name);
            // 基本数据类型
            if (cls == String.class || cls == Integer.class || cls == Long.class || cls.isPrimitive()) {
                StringValueCheck svc = methodParameter.getParameterAnnotation(StringValueCheck.class);
                if (svc != null) {
                    Object val = request.getAttribute(svc.name());
                    return val;
                } else {
                    if (value != null && (value.toString()).equals("NaN")) {
                        value = null;
                    }
                    if (cls == int.class || cls == long.class || cls == float.class || cls == double.class) {
                        return value == null ? 0 : StringUtils.string2Primitive(cls, value.toString());
                    } else {
                        return value == null ? null : StringUtils.string2Primitive(cls, value.toString());
                    }
                }
            }
            // list类型
            else if (cls == List.class) {
                if (value != null) {
                    ParameterizedType type = (ParameterizedType) methodParameter.getGenericParameterType();
                    // 获取当前List对象的泛型的类型
                    if (type.getActualTypeArguments()[0] instanceof ParameterizedType
                            && ((Class) ((ParameterizedType) (type.getActualTypeArguments()[0])).getRawType()) == Map.class) {
                        List<Map> list = JSONArray.parseArray(value.toString(), Map.class);
                        return list;
                    } else {
                        return JSONArray.parseArray(value.toString(), (Class) type.getActualTypeArguments()[0]);
                    }
                }
            } else if (cls == Date.class) {
                if (value != null) {
                    String text = StringUtils.trim(value.toString());
                    Date date = DateTool.parseDateAll(text);
                    return date;
                }
            }
            // 对象
            else {
                if (value != null) {
                    return JSONObject.parseObject(value.toString(), (Class) methodParameter.getGenericParameterType());
                }
            }
        } catch (Exception e) {
            logger.error("参数解析出错", e);
            throw e;
        }
        return null;
    }
}
