/**
 * 文件名：SessionInterceptor.java
 * 
 * 上海迈辰信息科技有限公司(http://www.maxeltech.com)
 * Copyright (c) 2012 MAXEL-TECH Corporation
 */
package com.maxeltech.smcc.interceptor;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.maxeltech.smcc.constant.common.MessageCode;
import com.maxeltech.smcc.exception.BusinessException;
import com.maxeltech.smcc.utils.common.HttpUtils;

/**
 * <p>
 * <li>Description:公共拦截器</li>
 * <li>$Author: chelongquan $</li>
 * <li>$Revision: 3968 $</li>
 * <li>$Date: 2018-08-22 17:34:24 +0800 (Wed, 22 Aug 2018) $</li>
 * 
 * @version 1.0
 */
public class SessionInterceptor implements HandlerInterceptor {

    /**
     * 日志对象
     */
    protected final Logger log = Logger.getLogger(getClass());

    public String[] allowUrls;// 不拦截的url，通过startwith进行判断的

    public String[] definiteTimeUrls;

    /**
     * session超时时间(分钟)
     */
    @Value("${smcc.sessionTimeOut}")
    private String sessionTimeOut;

    /**
     * 默认超时时间
     */
    private String DEFALUT_SESSION_TIME_OUT = "120";

    // /**
    // * 环境常量
    // */
    // @Value("${smcc_h5.env}")
    // private String env;
    /**
     * 设定allowUrls
     * 
     * @param allowUrls allowUrls
     */
    public void setAllowUrls(String[] allowUrls) {
        this.allowUrls = allowUrls;
    }

    /**
     * 设定definiteTimeUrls
     * 
     * @param definiteTimeUrls definiteTimeUrls
     */
    public void setDefiniteTimeUrls(String[] definiteTimeUrls) {
        this.definiteTimeUrls = definiteTimeUrls;
    }

    @Override
    public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
            throws Exception {
    }

    @Override
    public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
            throws Exception {
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object arg2) throws Exception {
        // session拦截
        String path = request.getServletPath();
        // 判断是否需要验证session
        if (!isValidateSession(path)) {
            if (path.startsWith("/operationUser/login")) {
                // 刷新session时间
                HttpUtils.putLastRequestTimeIntoSession(new Date().getTime());
            }
        } else { // 需要验证session方法
            Long userId = HttpUtils.getUserIdFromSession();
            if (isSessionTimeout(path) || null == userId) {
                // response.sendRedirect("login.action");
                throw new BusinessException(MessageCode.E_SESSION_TIME_OUT);
            }
        }
        return true;
    }

    /**
     * 判断session是否超时
     * 特殊说明：主要是因为有前台定时任务和后台交互的存在，所有会导致tomcat自身session一直都不会超时。所以我们需要一个自己判断是否超时的标准。即：定义的超时时间之内，如果全是前台定时任务的方法，即认为session超时
     * 
     * @param requestClassName 请求方法所在的类名
     * @param requestMethodName 请求方法名称
     * @return true session超时 false 未超时
     * @author zhaoyf
     */
    private boolean isSessionTimeout(String path) {
        Date date = new Date();
        if (isRefreshSession(path)) {
            HttpUtils.putLastRequestTimeIntoSession(date.getTime());
            return false;
        } else {
            Long currentDate = date.getTime();
            Long lastRequestTime = HttpUtils.getLastRequestTimeFromSession();
            if (lastRequestTime != null) {
                if (StringUtils.isEmpty(sessionTimeOut)) {
                    sessionTimeOut = DEFALUT_SESSION_TIME_OUT;
                }
                Long sessionTimeOutLong = Long.parseLong(sessionTimeOut);
                if (currentDate - lastRequestTime >= (1000 * 60 * sessionTimeOutLong)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 判断是否需要刷新session 只有当是用户手动发起的请求时 才需要刷新session 即：path不在definiteTimeUrls中 都需要刷新
     * 
     * @param path 请求的url
     * @return true:需要刷新 false:不需要刷新
     * @author zhaoyf
     */
    private boolean isRefreshSession(String path) {
        boolean isRefreshSession = true;
        if (null != definiteTimeUrls && definiteTimeUrls.length >= 1) {
            for (String url : definiteTimeUrls) {
                if (path.startsWith(url)) {
                    isRefreshSession = false;
                    break;
                }
            }
        }
        return isRefreshSession;
    }

    /**
     * 判断请求是否需要session验证
     * 
     * @param path 请求的url
     * @return true 需要session验证, false:不需要验证
     */
    public boolean isValidateSession(String path) {
        boolean isRequiredSession = true;
        if (null != allowUrls && allowUrls.length >= 1) {
            for (String url : allowUrls) {
                // TODO ckeditor跨域上传图片还有问题，先去的校验登录
                if (path.startsWith("/uploadImg/uploadImg")) {
                    return false;
                }
                // 其他正常校验
                if (path.startsWith(url)) {
                    isRequiredSession = false;
                    break;
                }
            }
        }
        return isRequiredSession;
    }
}
