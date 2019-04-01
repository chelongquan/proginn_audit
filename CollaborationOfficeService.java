/**
 * 文件名：CollaborationOfficeService.java
 * 
 * 上海迈辰信息科技有限公司(http://www.maxeltech.com)
 * Copyright (c) 2014 MAXEL-TECH Corporation
 */
package com.maxeltech.smcc.service.collaborationoffice;

import java.util.List;

import com.maxeltech.flexframework.dto.PageDto;
import com.maxeltech.smcc.exception.BusinessException;
import com.maxeltech.smcc.po.AppCollaborationOfficeRecheck;
import com.maxeltech.smcc.service.entity.CollaborationOfficeRecheckEntityService;
import com.maxeltech.smcc.vo.collaborationoffice.CollaborationOfficeRecheckQueryParamsVO;
import com.maxeltech.smcc.vo.collaborationoffice.CollaborationOfficeRecheckQueryResultVO;
import com.maxeltech.smcc.vo.common.ExportExcelVO;
import com.maxeltech.smcc.vo.common.IDStringMappingVO;
import com.maxeltech.smcc.vo.page.PageParameterVo;

/**
 * <p>
 * <li>Description:联合办公Service</li>
 * <li>$Author: chelongquan $</li>
 * <li>$Revision: 5003 $</li>
 * <li>$Date: 2019-01-15 14:29:18 +0800 (Tue, 15 Jan 2019) $</li>
 * 
 * @version 1.0
 */
public interface CollaborationOfficeService extends CollaborationOfficeRecheckEntityService {

    /**
     * 通过联合办公变更工单编号查询实体对象
     * 
     * @param ChrProcessNumber 联合办公变更工单编号
     * @return 返回查询实体对象
     * @throws BusinessException 数据库操作业务异常
     */
    public AppCollaborationOfficeRecheck getByChrProcessNumber(String ChrProcessNumber) throws BusinessException;

    /**
     * 查询复核单对应的变更单的用户名
     * 
     * @param chrProcessNumber 联合办公变更编号
     * @return 变更单的用户名
     * @throws BusinessException数据库异常
     */
    public String getApplicantNameBySourceExternalId(String chrProcessNumber) throws BusinessException;

    /**
     * 查询复核单列表
     * 
     * @param paramsVo 查询条件
     * @param pageParameterVo 分页条件
     * @return 工单分页数据
     * @throws BusinessException 业务异常
     */
    public PageDto<CollaborationOfficeRecheckQueryResultVO> queryCollaborationOfficeRecheckList(
            CollaborationOfficeRecheckQueryParamsVO paramsVo, PageParameterVo pageParameterVo) throws BusinessException;

    /**
     * 查询5种提示方式的数量
     * 
     * @param InspectionGroupQueryParamsVO 查询条件
     * @return 工单任务数量数组 [全部,代发复核单，待复核，已复核待审，已审]
     * @throws BusinessException 业务异常
     */
    public List<Integer> queryRecheckCounts(
            CollaborationOfficeRecheckQueryParamsVO collaborationOfficeRecheckQueryParamsVO) throws BusinessException;

    /**
     * 根据查询条件获得复核单数量
     * 
     * @param changeAssessmentQueryParamsVO 查询条件
     * @return 待处理的评估工单数量
     * @throws BusinessException 业务异常
     */
    public Integer getCollaborationOfficeRecheckQueryCount(
            CollaborationOfficeRecheckQueryParamsVO collaborationOfficeRecheckQueryParamsVO) throws BusinessException;

    /**
     * 查询已复核待审状态的复核单数量(菜单显示使用)
     * 
     * @param userId 登陆人员Id
     * @return 待处理的评估工单数量
     * @throws BusinessException 业务异常
     */
    public Integer getRecheckStatusCount(Long userId) throws BusinessException;

    /**
     * 复核（处理）复核单
     * 
     * @param po 复核单参数VO
     * @return 查询结果列表
     * @throws BusinessException 业务异常
     */
    public void handleCollaborationOfficeRecheck(AppCollaborationOfficeRecheck po) throws BusinessException;

    /**
     * 根据复核单ID查询详细信息
     * 
     * @param reCheckId 复核单ID
     * @return 复核单详细信息
     * @throws BusinessException 业务异常
     */
    public CollaborationOfficeRecheckQueryResultVO queryDetailByReCheckId(Long reCheckId) throws BusinessException;

    /**
     * 查找[存在未完成复核单]的工单ID、编号集合
     * 
     * @param ticketIdList 工单ID集合
     * @return 存在未完成复核单的工单ID、编号集合
     * @throws BusinessException 业务异常
     */
    public List<IDStringMappingVO> listTheTicketWhoHasUnfinishedRecheckTicket(List<Long> ticketIdList)
            throws BusinessException;

    /**
     * 导出复核单详细信息
     * 
     * @param reCheckIds 工单ID集合
     * @param paramsVO 查询工单参数
     * @throws BusinessException 业务异常
     */
    public ExportExcelVO exportCollaborationOfficeRecheckData(CollaborationOfficeRecheckQueryParamsVO paramsVO,
            List<Long> reCheckIds) throws BusinessException;
}
