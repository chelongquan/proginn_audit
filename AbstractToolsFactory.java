/**
 * 文件名：AbstractToolsFactory.java
 * 
 * 上海迈辰信息科技有限公司(http://www.maxeltech.com)
 * Copyright (c) 2012 MAXEL-TECH Corporation
 */
package com.maxeltech.smcc.plugins.webservice;

import com.maxeltech.smcc.exception.BusinessException;
import com.maxeltech.smcc.po.AppModule;

/**
 * <p>
 * <li>Description:WebService抽象工具工厂类</li>
 * <li>$Author: chelongquan $</li>
 * <li>$Revision: 44658 $</li>
 * <li>$Date: 2013-08-20 15:25:54 +0800 (周二, 20 八月 2013) $</li>
 * 
 * @version 1.0
 */
public abstract class AbstractToolsFactory {

    /**
     * 接口模块实体
     */
    protected AppModule appModule;

    /**
     * 根据模块实体初始化工具连接信息
     * 
     * @param appModuleParams 模块参数实体（需同时设置code和belongType属性）
     * @return 模块初始化标示(True:成功;False:失败)
     * @throws BusinessException 业务操作异常
     */
    protected abstract boolean initTool(AppModule appModuleParams) throws BusinessException;
}
