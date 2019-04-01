/**
 * 文件名：ToolsFactory.java
 * 
 * 上海迈辰信息科技有限公司(http://www.maxeltech.com)
 * Copyright (c) 2012 MAXEL-TECH Corporation
 */
package com.maxeltech.smcc.plugins.webservice;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.maxeltech.smcc.constant.common.Constant;
import com.maxeltech.smcc.exception.BusinessException;
import com.maxeltech.smcc.po.AppModule;
import com.maxeltech.smcc.service.interfacemodule.InterfaceModuleService;

/**
 * <p>
 * <li>Description:WebService工具工厂类</li>
 * <li>$Author: chelongquan $</li>
 * <li>$Revision: 44658 $</li>
 * <li>$Date: 2013-08-20 15:25:54 +0800 (周二, 20 八月 2013) $</li>
 * 
 * @version 1.0
 */
public class ToolsFactory extends AbstractToolsFactory {

    /**
     * 日志对象
     */
    private Log log = LogFactory.getLog(getClass());

    /**
     * 成功常量
     */
    public static final String SUCCESS = "SUCCESS";

    /**
     * 失败常量
     */
    public static final String FAILED = "FAILED";

    /**
     * 接口模块Service
     */
    @Resource
    private InterfaceModuleService interfaceModuleService;

    /**
     * 根据模块实体初始化工具连接信息
     * 
     * @param appModuleParams 模块参数实体（需同时设置code和belongType属性）
     * @return 模块初始化标示(True:成功;False:失败)
     * @throws BusinessException 业务操作异常
     */
    @Override
    protected boolean initTool(AppModule appModuleParams) throws BusinessException {
        appModule = interfaceModuleService.getInterfaceModuleByModuleCodeAndType(appModuleParams);
        if (null != appModule && null != appModule.getEnableFlag()
                && Constant.YES.equals(appModule.getEnableFlag().trim())) {
            // 模块为启用状态
            log.debug("<---成功初始化[" + appModule.getName().trim() + "]工具--->");
            return true;
        }
        return false;
    }
}
