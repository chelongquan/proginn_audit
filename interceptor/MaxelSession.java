/**
 * 文件名：MaxelSession.java
 * 
 * 上海迈辰信息科技有限公司(http://www.maxeltech.com)
 * Copyright (c) 2014 MAXEL-TECH Corporation
 */
package com.maxeltech.smcc.interceptor;

import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import com.maxeltech.smcc.constant.common.Constant;
import com.maxeltech.smcc.utils.common.HttpUtils;

/**
 * <p>
 * <li>Description:自定义Session（由于FlexSession无法序列化，所以利用本类绕过session管理器，直连Redis存取会话数据）</li>
 * <li>$Author$</li>
 * <li>$Revision$</li>
 * <li>$Date$</li>
 * 
 * @version 1.0
 */
@SuppressWarnings("deprecation")
public class MaxelSession implements HttpSession {

    /**
     * 日志对象
     */
    private static Log log = LogFactory.getLog(MaxelSession.class);

    /**
     * 会话超时分钟数
     */
    private static final String SESSION_TIMEOUT_MINUTES = ResourceBundle.getBundle("config/springinit").getString(
            "smcc.sessionTimeOut");

    /**
     * Redis服务器地址
     */
    private static final String REDIS_URL = ResourceBundle.getBundle("config/springinit").getString("redis.url");

    /**
     * Redis服务器密码
     */
    private static final String REDIS_PASSWORD = ResourceBundle.getBundle("config/springinit").getString(
            "redis.password");

    /**
     * Redis session共享启用标记
     */
    private static final String redisSessionEnableFlag = ResourceBundle.getBundle("config/springinit").getString(
            "redis.session.enable.flag");

    /**
     * 单实例对象
     */
    private static MaxelSession instance;

    /**
     * Redisson客户端
     */
    private static RedissonClient redissonClient = createClient();

    /**
     * 构建Redisson客户端
     * 
     * @return Redisson客户端
     */
    private static RedissonClient createClient() {
        if (Constant.YES.equals(redisSessionEnableFlag)) {
            Config config = new Config();
            config.useSingleServer().setAddress(REDIS_URL);
            config.useSingleServer().setPassword(REDIS_PASSWORD);
            return Redisson.create(config);
        }
        return null;
    }

    /**
     * 获取单实例
     * 
     * @return 单实例
     */
    public static MaxelSession getInstance() {
        if (null == instance) {
            instance = new MaxelSession();
        }
        return instance;
    }

    /**
     * 销毁资源（仅在关闭Tomcat时需要调用）
     */
    public static void destroy() {
        if (null != instance) {
            instance = null;
        }
        if (null != redissonClient) {
            redissonClient.shutdown();
            redissonClient = null;
        }
    }

    /**
     * 从Redis获取会话数据
     * 
     * @param key 数据键
     * @return 数据值
     */
    @Override
    public Object getAttribute(String key) {
        HttpSession session = HttpUtils.getHttpSession();
        if (session != null) {
            String sessionId = session.getId();
            return getAttribute(sessionId, key);
        }
        return null;
    }

    /**
     * 从Redis获取会话数据
     * 
     * @param sessionId 会话ID
     * @param key 数据键
     * @return 数据值
     */
    public Object getAttribute(String sessionId, String key) {
        if (null == sessionId) {
            sessionId = "";
        }
        log.info("MaxelSession.getAttribute，sessionId:" + sessionId);
        log.info("MaxelSession.getAttribute，key:" + key);
        RBucket<Object> keyObject = redissonClient.getBucket(sessionId + "_" + key);
        log.info("MaxelSession.getAttribute，keyObject.get():" + keyObject.get());
        return keyObject.get();
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getCreationTime() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getLastAccessedTime() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getMaxInactiveInterval() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public ServletContext getServletContext() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HttpSessionContext getSessionContext() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getValue(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String[] getValueNames() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void invalidate() {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean isNew() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void putValue(String arg0, Object arg1) {
        // TODO Auto-generated method stub
    }

    @Override
    public void removeAttribute(String arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void removeValue(String arg0) {
        // TODO Auto-generated method stub
    }

    /**
     * 保存会话数据至Redis
     * 
     * @param key 数据键
     * @param value 数据值
     */
    @Override
    public void setAttribute(String key, Object value) {
        HttpSession session = HttpUtils.getHttpSession();
        if (session != null) {
            String sessionId = session.getId();
            setAttribute(sessionId, key, value);
        }
    }

    /**
     * 保存会话数据至Redis
     * 
     * @param sessionId 会话ID
     * @param key 数据键
     * @param value 数据值
     */
    public void setAttribute(String sessionId, String key, Object value) {
        if (null == sessionId) {
            sessionId = "";
        }
        log.info("MaxelSession.setAttribute，sessionId:" + sessionId);
        log.info("MaxelSession.setAttribute，key:" + key);
        log.info("MaxelSession.setAttribute，value:" + value);
        RBucket<Object> keyObject = redissonClient.getBucket(sessionId + "_" + key);
        if (null == value) {
            keyObject.delete();
        } else {
            keyObject.set(value, Long.parseLong(SESSION_TIMEOUT_MINUTES), TimeUnit.MINUTES);
        }
    }

    @Override
    public void setMaxInactiveInterval(int arg0) {
        // TODO Auto-generated method stub
    }
}
