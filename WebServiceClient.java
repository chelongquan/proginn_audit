/**
 * 文件名：WebServiceClient.java
 * 
 * 上海迈辰信息科技有限公司(http://www.maxeltech.com)
 * Copyright (c) 2012 MAXEL-TECH Corporation
 */
package com.maxeltech.smcc.plugins.webservice;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.log4j.Logger;

/**
 * <p>
 * <li>Description:WebService调用通用客户端</li>
 * <li>$Author: xuxie $</li>
 * <li>$Revision: 68118 $</li>
 * <li>$Date: 2017-06-07 13:32:26 +0800 (周三, 07 六月 2017) $</li>
 * 
 * @version 1.0
 */
public final class WebServiceClient {

    /**
     * 日志对象
     */
    private static Logger log = Logger.getLogger(WebServiceClient.class);

    /**
     * CXF客户端键值对（key为WEB服务地址，value为CXF客户端）
     */
    private static Map<String, Client> cxfClientMap = null;

    /**
     * CXF客户端锁（防止并发生成相同WebService地址对应的客户端）
     */
    public static Object lock = new Object();

    /**
     * 通用调用WebService
     * 
     * @param webServiceUrl WEB服务地址
     * @param methodName 方法名
     * @param params 方法参数数组（大多数情况下只会有一个参数，即数组长度为1）
     * @return 返回值数组（大多数情况下取第0个元素即可）
     * @throws Exception
     */
    @SuppressWarnings("all")
    public static Object[] invokeWebService(String webServiceUrl, String methodName, String[] params) throws Exception {
        log.info(">>>>>>>>>>>>>>>>[cxf升级测试]WebServiceClient.invokeWebService开始");
        log.info(">>>>>>>>>>>>>>>>[cxf升级测试]webServiceUrl=" + ((null == webServiceUrl) ? "空" : webServiceUrl));
        log.info(">>>>>>>>>>>>>>>>[cxf升级测试]methodName=" + ((null == methodName) ? "空" : methodName));
        log.info(">>>>>>>>>>>>>>>>[cxf升级测试]params=" + ArrayUtils.toString(params, "空"));
        if (StringUtils.isEmpty(webServiceUrl)) {
            throw new NullPointerException("Web Service Url can not be null");
        }
        Client cxfClient = null;
        webServiceUrl = processUrl(webServiceUrl);
        synchronized (lock) {
            if (null == cxfClientMap) {
                cxfClientMap = new ConcurrentHashMap<String, Client>();
            }
            if (cxfClientMap.containsKey(webServiceUrl)) {
                cxfClient = cxfClientMap.get(webServiceUrl);
                log.info(">>>>>>>>>>>>>>>>[cxf升级测试]重用CXF客户端");
            } else {
                JaxWsDynamicClientFactory dynamicClientFactory = JaxWsDynamicClientFactory.newInstance();
                cxfClient = dynamicClientFactory.createClient(webServiceUrl, WebServiceClient.class.getClassLoader());
                cxfClientMap.put(webServiceUrl, cxfClient);
                log.info(">>>>>>>>>>>>>>>>[cxf升级测试]新建CXF客户端");
            }
        }
        Object[] result = cxfClient.invoke(methodName, params);
        log.info(">>>>>>>>>>>>>>>>[cxf升级测试]result=" + ArrayUtils.toString(result, "空"));
        log.info(">>>>>>>>>>>>>>>>[cxf升级测试]WebServiceClient.invokeWebService结束");
        return result;
    }

    /**
     * 处理设置的webservice
     * 
     * @param webServiceUrl
     * @return 返回含有?wsdl的地址
     */
    private static String processUrl(String webServiceUrl) {
        if (StringUtils.isEmpty(webServiceUrl)) {
            return null;
        }
        if (webServiceUrl.endsWith("?wsdl")) {
            return webServiceUrl;
        }
        if (webServiceUrl.endsWith("/")) {
            webServiceUrl = webServiceUrl.substring(0, webServiceUrl.length() - 1);
        }
        return webServiceUrl + "?wsdl";
    }
}