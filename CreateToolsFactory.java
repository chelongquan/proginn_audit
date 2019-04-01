/**
 * 文件名：CreatToolsFactory.java
 * 
 * 上海迈辰信息科技有限公司(http://www.maxeltech.com)
 * Copyright (c) 2012 MAXEL-TECH Corporation
 */
package com.maxeltech.smcc.plugins.webservice;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.maxeltech.smcc.plugins.webservice.audit.AuditTool;
import com.maxeltech.smcc.plugins.webservice.audit.update.NewAuditTool;
import com.maxeltech.smcc.plugins.webservice.client.dpms.DpmsTool;
import com.maxeltech.smcc.plugins.webservice.client.sms.SMSTool;
import com.maxeltech.smcc.plugins.webservice.server.itm.ITManagerTool;
import com.maxeltech.smcc.plugins.webservice.ucm.UcmTool;

/**
 * <p>
 * <li>Description:创建工具工厂类</li>
 * <li>$Author: chelongquan $</li>
 * <li>$Revision: 62170 $</li>
 * <li>$Date: 2016-03-07 13:10:17 +0800 (周一, 07 三月 2016) $</li>
 * 
 * @version 1.0
 */
@Component
public class CreateToolsFactory {

    /**
     * 短信平台工具
     */
    @Resource
    private SMSTool sMSTool;

    /**
     * ITManager工具
     */
    @Resource
    private ITManagerTool iTManagerTool;

    /**
     * UCM密码管理系统工具
     */
    @Resource
    private UcmTool ucmTool;

    /**
     * DPMS动态密码管理系统工具
     */
    @Resource
    private DpmsTool dpmsTool;

    /**
     * 审计系统工具
     */
    @Resource
    private AuditTool auditTool;

    /**
     * 新审计系统工具
     */
    @Resource
    private NewAuditTool newAuditTool;

    /**
     * 创建短信平台工具
     * 
     * @return 短信平台工具
     */
    public SMSTool createSMSTool() {
        return this.sMSTool;
    }

    /**
     * 创建ITManager工具
     * 
     * @return ITManager工具
     */
    public ITManagerTool createITManagerTool() {
        return this.iTManagerTool;
    }

    /**
     * 创建UCM密码管理系统工具
     * 
     * @return UCM密码管理系统工具
     */
    public UcmTool createUcmTool() {
        return this.ucmTool;
    }

    /**
     * 创建DPMS动态密码管理系统工具
     * 
     * @return DPMS动态密码管理系统工具
     */
    public DpmsTool createDpmsTool() {
        return this.dpmsTool;
    }

    /**
     * 创建审计系统工具
     * 
     * @return 审计系统工具
     */
    public AuditTool createAuditTool() {
        return this.auditTool;
    }

    /**
     * 创建新审计系统工具
     * 
     * @return 新审计系统工具
     * @return
     */
    public NewAuditTool createNewAuditTool() {
        return this.newAuditTool;
    }
}
