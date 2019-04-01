/**
 * 文件名：CollaborationOfficeServiceImpl.java
 * 
 * 上海迈辰信息科技有限公司(http://www.maxeltech.com)
 * Copyright (c) 2014 MAXEL-TECH Corporation
 */
package com.maxeltech.smcc.service.collaborationoffice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.maxeltech.flexframework.dto.PageDto;
import com.maxeltech.smcc.constant.collaborationoffice.CollaborationOfficeConstant;
import com.maxeltech.smcc.constant.common.Constant;
import com.maxeltech.smcc.constant.common.MessageCode;
import com.maxeltech.smcc.constant.operationuser.OperationUserConstant;
import com.maxeltech.smcc.constant.permission.PermissionConstant;
import com.maxeltech.smcc.constant.ticket.TicketConstant;
import com.maxeltech.smcc.constant.tickettask.TicketTaskConstant;
import com.maxeltech.smcc.dao.CollaborationOfficeRecheckDao;
import com.maxeltech.smcc.exception.BusinessException;
import com.maxeltech.smcc.po.AppCollaborationOfficeRecheck;
import com.maxeltech.smcc.po.AppOperationDepartment;
import com.maxeltech.smcc.po.AppOperationUser;
import com.maxeltech.smcc.service.changeticket.ChangeTicketService;
import com.maxeltech.smcc.service.entity.impl.CollaborationOfficeRecheckEntityServiceImpl;
import com.maxeltech.smcc.service.operationdepartment.OperationDepartmentService;
import com.maxeltech.smcc.service.operationuser.OperationUserService;
import com.maxeltech.smcc.utils.common.ConverterUtils;
import com.maxeltech.smcc.utils.common.DateTool;
import com.maxeltech.smcc.utils.common.ExportExcel;
import com.maxeltech.smcc.utils.common.NewExportExcel;
import com.maxeltech.smcc.utils.common.ValidateUtils;
import com.maxeltech.smcc.vo.collaborationoffice.CollaborationOfficeRecheckExportVO;
import com.maxeltech.smcc.vo.collaborationoffice.CollaborationOfficeRecheckQueryParamsVO;
import com.maxeltech.smcc.vo.collaborationoffice.CollaborationOfficeRecheckQueryResultVO;
import com.maxeltech.smcc.vo.collaborationoffice.CollaborationOfficeRecheckTicketAndTicketTask;
import com.maxeltech.smcc.vo.common.ExportExcelVO;
import com.maxeltech.smcc.vo.common.IDStringMappingVO;
import com.maxeltech.smcc.vo.page.PageParameterVo;

/**
 * <p>
 * <li>Description:联合办公服务实现类</li>
 * <li>$Author: luguoce $</li>
 * <li>$Revision: 5147 $</li>
 * <li>$Date: 2019-02-13 17:43:51 +0800 (Wed, 13 Feb 2019) $</li>
 * 
 * @version 1.0
 */
@Service
public class CollaborationOfficeServiceImpl extends CollaborationOfficeRecheckEntityServiceImpl implements
        CollaborationOfficeService {

    /**
     * 联合办公复核单dao
     */
    @Resource
    private CollaborationOfficeRecheckDao collaborationOfficeRecheckDao;

    @Resource
    private OperationDepartmentService operationDepartmentService;

    @Resource
    private OperationUserService operationUserService;

    @Resource
    private ChangeTicketService changeTicketService;

    /**
     * 通过联合办公变更工单编号查询实体对象
     * 
     * @param ChrProcessNumber 联合办公变更工单编号
     * @return 返回查询实体对象
     * @throws BusinessException 数据库操作业务异常
     */
    @Override
    public AppCollaborationOfficeRecheck getByChrProcessNumber(String ChrProcessNumber) throws BusinessException {
        return collaborationOfficeRecheckDao.getByChrProcessNumber(ChrProcessNumber);
    }

    /**
     * 查询复核单对应的变更单的用户名
     * 
     * @param chrProcessNumber 联合办公变更编号
     * @return 变更单的用户名
     * @throws BusinessException数据库异常
     */
    @Override
    public String getApplicantNameBySourceExternalId(String chrProcessNumber) throws BusinessException {
        List<String> userNames = collaborationOfficeRecheckDao.getApplicantNameBySourceExternalId(chrProcessNumber);
        if (ValidateUtils.isEmptyCollection(userNames)) {
            return "";
        }
        return userNames.get(0);
    }

    /**
     * 查询复核单列表
     * 
     * @param paramsVo 查询条件
     * @param pageParameterVo 分页条件
     * @return 工单分页数据
     * @throws BusinessException 业务异常
     */
    @Override
    public PageDto<CollaborationOfficeRecheckQueryResultVO> queryCollaborationOfficeRecheckList(
            CollaborationOfficeRecheckQueryParamsVO paramsVo, PageParameterVo pageParameterVo) throws BusinessException {
        List<CollaborationOfficeRecheckQueryResultVO> dataList = null;
        if (paramsVo == null) {
            paramsVo = new CollaborationOfficeRecheckQueryParamsVO();
        }
        if (pageParameterVo == null) {
            pageParameterVo = new PageParameterVo();
        }
        // 复核单查看权限
        List<Long> operationDepartmentIdList = this.getOperationDepartmentIdList(paramsVo.getUserType(),
                paramsVo.getUserId(), PermissionConstant.VIEW_CHANGE_RECHECK);
        if (ValidateUtils.isEmptyCollection(operationDepartmentIdList)) {
            return new PageDto<CollaborationOfficeRecheckQueryResultVO>(pageParameterVo.getPageNo(),
                    pageParameterVo.getPageSize(), 0, dataList);
        }
        // 设置营运组织ID集合为查询条件
        paramsVo.setOperationDepartmentIdList(ConverterUtils.listToString(operationDepartmentIdList));
        // 修改时间：2017-12-15 18:35:22 判断"待发复核单"要包含工单任务完成状态
        String reCheckStatusParam = paramsVo.getRecheckStatus();
        if (StringUtils.isNotEmpty(reCheckStatusParam)
                && reCheckStatusParam.indexOf("" + CollaborationOfficeConstant.RECHECKSTATUS_WAIT_SENT) != -1) {
            paramsVo.setOverTicketFlag(Constant.YES);
        }
        // 查询数据
        dataList = collaborationOfficeRecheckDao.queryCollaborationOfficeRecheckList(paramsVo, pageParameterVo);
        // 如果不在列表第一页并且列表数据为空，需要自动跳转到上一页获取列表数据
        if (pageParameterVo.getPageNo() > 1 && CollectionUtils.isEmpty(dataList)) {
            pageParameterVo.setPageNo(pageParameterVo.getPageNo() - 1);
            dataList = collaborationOfficeRecheckDao.queryCollaborationOfficeRecheckList(paramsVo, pageParameterVo);
        }
        int count = collaborationOfficeRecheckDao.getCollaborationOfficeRecheckQueryCount(paramsVo);
        // 设置返回集合中，工单和工单任务的的列表参数
        this.setTicketAndTaskColumn(dataList);
        // 返回工单列表数据
        return new PageDto<CollaborationOfficeRecheckQueryResultVO>(pageParameterVo.getPageNo(),
                pageParameterVo.getPageSize(), count, dataList);
    }

    /**
     * 设置返回集合中，工单和工单任务的的列表参数，复核到期时间参数
     * 
     * @param reCheckIdsList
     * @param dataList
     * @return
     */
    private void setTicketAndTaskColumn(List<CollaborationOfficeRecheckQueryResultVO> dataList)
            throws BusinessException {
        if (ValidateUtils.isEmptyCollection(dataList)) {
            return;
        }
        List<Long> reCheckIdsList = Lists.newArrayList();
        for (CollaborationOfficeRecheckQueryResultVO vo : dataList) {
            reCheckIdsList.add(vo.getId());
        }
        // 设置需要的工单和工单任务的显示字段
        List<CollaborationOfficeRecheckTicketAndTicketTask> tmpResult = collaborationOfficeRecheckDao
                .getTicketAndTaskListByReCheckIds(ConverterUtils.listToString(reCheckIdsList));
        if (!ValidateUtils.isEmptyCollection(tmpResult)) {
            Map<Long, List<CollaborationOfficeRecheckTicketAndTicketTask>> reCheckMap = Maps.newHashMap();// key:复核单Id,value:中间vo
            for (CollaborationOfficeRecheckTicketAndTicketTask o : tmpResult) {
                List<CollaborationOfficeRecheckTicketAndTicketTask> list = null;
                if (reCheckMap.keySet().contains(o.getReCheckId())) {
                    list = reCheckMap.get(o.getReCheckId());
                } else {
                    list = Lists.newArrayList();
                }
                list.add(o);
                reCheckMap.put(o.getReCheckId(), list);
            }
            // 设置每个复核单对应的工单编号和任务单编号map集合
            Map<Long, Map<String, List<String>>> allMap = Maps.newHashMap();// <复核单Id,<工单编号，任务单编号list>>
            for (Long recheckid : reCheckMap.keySet()) {
                Map<String, List<String>> ticketAndTaskSerialMap = Maps.newHashMap();// <工单编号，任务单编号list>
                List<CollaborationOfficeRecheckTicketAndTicketTask> tmpList = reCheckMap.get(recheckid);
                for (CollaborationOfficeRecheckTicketAndTicketTask task : tmpList) {
                    List<String> list = null;
                    if (ticketAndTaskSerialMap.keySet().contains(task.getTicketSerialNumber())) {
                        list = ticketAndTaskSerialMap.get(task.getTicketSerialNumber());
                    } else {
                        list = Lists.newArrayList();
                    }
                    list.add(task.getTaskSerialNumber());
                    ticketAndTaskSerialMap.put(task.getTicketSerialNumber(), list);
                }
                allMap.put(recheckid, ticketAndTaskSerialMap);
            }
            // 开始设置返回对象的参数
            for (CollaborationOfficeRecheckQueryResultVO resultVO : dataList) {
                // 设置变更编码为--> 变更编号+工单任务编号
                StringBuffer sbBuffer = new StringBuffer();
                List<String> serialNumberList = Lists.newArrayList();
                Map<String, List<String>> serialMap = allMap.get(resultVO.getId());
                for (String tickserial : serialMap.keySet()) {
                    StringBuffer ticketBuffer = new StringBuffer();
                    ticketBuffer.append(tickserial);
                    serialNumberList.add(tickserial);
                    List<String> taskSerialList = serialMap.get(tickserial);
                    for (String taskSerial : taskSerialList) {
                        if (StringUtils.isNotEmpty(taskSerial)) {
                            ticketBuffer.append("  " + taskSerial);
                            serialNumberList.add(taskSerial);
                        }
                    }
                    sbBuffer.append(ticketBuffer.toString() + "\r");
                }
                resultVO.setTicketAndTaskSerialNumberAlias(sbBuffer.toString());
                resultVO.setSerialNumberList(serialNumberList);
                // 设置变更结果
                sbBuffer = new StringBuffer();
                List<CollaborationOfficeRecheckTicketAndTicketTask> tmpList = reCheckMap.get(resultVO.getId());
                for (CollaborationOfficeRecheckTicketAndTicketTask vo : tmpList) {
                    if (StringUtils.isNotBlank(vo.getTaskSerialNumber())) {
                        sbBuffer.append(vo.getTaskSerialNumber() == null ? "" : vo.getTaskSerialNumber() + ":<br>");
                        sbBuffer.append(vo.getCloseTime() == null ? " " : " "
                                + DateTool.formatDateTime(vo.getCloseTime()));
                        sbBuffer.append(" " + vo.getOwnerUserName() == null ? "" : vo.getOwnerUserName() + ":<br>");
                        if (StringUtils.isNotEmpty(vo.getProcessDescriptionText())) {
                            sbBuffer.append(vo.getProcessDescriptionText() + ":<br>");// 2017-11-28 9:53:41 新增加处理描述
                        }
                        if (TicketTaskConstant.CLOSECODE_MAP.keySet().contains(vo.getCloseCode())) {
                            sbBuffer.append(TicketTaskConstant.CLOSECODE_MAP.get(vo.getCloseCode()) + "<br>");
                        }
                    }
                }
                resultVO.setChangeResult(sbBuffer.toString());
                resultVO.setSubject(tmpList.size() > 0 ? tmpList.get(0).getSubject() : "");
            }
        }
    }

    /**
     * 根据查询条件获得复核单数量
     * 
     * @param changeAssessmentQueryParamsVO 查询条件
     * @return 待处理的评估工单数量
     * @throws BusinessException 业务异常
     */
    @Override
    public Integer getCollaborationOfficeRecheckQueryCount(
            CollaborationOfficeRecheckQueryParamsVO collaborationOfficeRecheckQueryParamsVO) throws BusinessException {
        List<Long> operationDepartmentIdList = this.getOperationDepartmentIdList(
                collaborationOfficeRecheckQueryParamsVO.getUserType(),
                collaborationOfficeRecheckQueryParamsVO.getUserId(), PermissionConstant.VIEW_CHANGE_RECHECK);
        if (ValidateUtils.isEmptyCollection(operationDepartmentIdList)) {
            return 0;
        }
        // 设置营运组织ID集合为查询条件
        collaborationOfficeRecheckQueryParamsVO.setOperationDepartmentIdList(ConverterUtils
                .listToString(operationDepartmentIdList));
        // 修改时间：2017-12-15 18:35:22 判断"待发复核单"要包含工单任务完成状态
        String reCheckStatusParam = collaborationOfficeRecheckQueryParamsVO.getRecheckStatus();
        if (StringUtils.isNotEmpty(reCheckStatusParam)
                && reCheckStatusParam.indexOf("" + CollaborationOfficeConstant.RECHECKSTATUS_WAIT_SENT) != -1) {
            collaborationOfficeRecheckQueryParamsVO.setOverTicketFlag(Constant.YES);
        }
        return collaborationOfficeRecheckDao
                .getCollaborationOfficeRecheckQueryCount(collaborationOfficeRecheckQueryParamsVO);
    }

    /**
     * 查询5种提示方式的数量
     * 
     * @param InspectionGroupQueryParamsVO 查询条件
     * @return 工单任务数量数组 [全部,代发复核单，待复核，已复核待审，已审]
     * @throws BusinessException 业务异常
     */
    @Override
    public List<Integer> queryRecheckCounts(
            CollaborationOfficeRecheckQueryParamsVO collaborationOfficeRecheckQueryParamsVO) throws BusinessException {
        List<Long> operationDepartmentIdList = this.getOperationDepartmentIdList(
                collaborationOfficeRecheckQueryParamsVO.getUserType(),
                collaborationOfficeRecheckQueryParamsVO.getUserId(), PermissionConstant.VIEW_CHANGE_RECHECK);
        List<Integer> countList = new ArrayList<Integer>(5);
        if (ValidateUtils.isEmptyCollection(operationDepartmentIdList)) {
            return countList;
        }
        // 设置营运组织ID集合为查询条件
        collaborationOfficeRecheckQueryParamsVO.setOperationDepartmentIdList(ConverterUtils
                .listToString(operationDepartmentIdList));
        collaborationOfficeRecheckQueryParamsVO.setRecheckStatus("");
        countList.add(collaborationOfficeRecheckDao
                .getCollaborationOfficeRecheckQueryCount(collaborationOfficeRecheckQueryParamsVO));
        collaborationOfficeRecheckQueryParamsVO.setRecheckStatus(""
                + CollaborationOfficeConstant.RECHECKSTATUS_WAIT_SENT);
        collaborationOfficeRecheckQueryParamsVO.setOverTicketFlag(Constant.YES);
        countList.add(collaborationOfficeRecheckDao
                .getCollaborationOfficeRecheckQueryCount(collaborationOfficeRecheckQueryParamsVO));
        collaborationOfficeRecheckQueryParamsVO.setOverTicketFlag(null);
        collaborationOfficeRecheckQueryParamsVO.setRecheckStatus(""
                + CollaborationOfficeConstant.RECHECKSTATUS_WAIT_RECHECK);
        countList.add(collaborationOfficeRecheckDao
                .getCollaborationOfficeRecheckQueryCount(collaborationOfficeRecheckQueryParamsVO));
        collaborationOfficeRecheckQueryParamsVO.setRecheckStatus(""
                + CollaborationOfficeConstant.RECHECKSTATUS_HASRECHECK_WAITCHECK);
        countList.add(collaborationOfficeRecheckDao
                .getCollaborationOfficeRecheckQueryCount(collaborationOfficeRecheckQueryParamsVO));
        collaborationOfficeRecheckQueryParamsVO.setRecheckStatus(""
                + CollaborationOfficeConstant.RECHECKSTATUS_HAS_CHECK);
        countList.add(collaborationOfficeRecheckDao
                .getCollaborationOfficeRecheckQueryCount(collaborationOfficeRecheckQueryParamsVO));
        return countList;
    }

    /**
     * 查询已复核待审状态的复核单数量(菜单显示使用)
     * 
     * @param changeAssessmentQueryParamsVO 查询条件
     * @return 待处理的评估工单数量
     * @throws BusinessException 业务异常
     */
    @Override
    public Integer getRecheckStatusCount(Long userId) throws BusinessException {
        AppOperationUser user = operationUserService.getById(userId);
        CollaborationOfficeRecheckQueryParamsVO queryParam = new CollaborationOfficeRecheckQueryParamsVO();
        if (OperationUserConstant.ADMIN_TYPE.equals(user.getType())) {
            return 0;
        }
        List<Long> operationDepartmentIdList = this.getOperationDepartmentIdList(user.getType(), userId,
                PermissionConstant.VIEW_CHANGE_RECHECK);
        if (ValidateUtils.isEmptyCollection(operationDepartmentIdList)) {
            return 0;
        }
        // 设置营运组织ID集合为查询条件
        queryParam.setOperationDepartmentIdList(ConverterUtils.listToString(operationDepartmentIdList));
        queryParam.setUserId(userId);
        queryParam.setUserType(user.getType());
        queryParam.setRecheckStatus("" + CollaborationOfficeConstant.RECHECKSTATUS_HASRECHECK_WAITCHECK);
        return collaborationOfficeRecheckDao.getCollaborationOfficeRecheckQueryCount(queryParam);
    }

    /**
     * 复核（处理）复核单
     * 
     * @param po 复核单参数VO
     * @return 查询结果列表
     * @throws BusinessException 业务异常
     */
    @Override
    public void handleCollaborationOfficeRecheck(AppCollaborationOfficeRecheck po) throws BusinessException {
        if (po == null || po.getId() == null || po.getId() <= 0) {
            throw new BusinessException(MessageCode.E_PARAM_IS_NULL);
        }
        AppCollaborationOfficeRecheck source = collaborationOfficeRecheckDao.getById(po.getId());
        if (source == null) {
            throw new BusinessException(MessageCode.E_TARGET_NOT_EXIST, ConverterUtils.dynamicArrayToList("复核单"));
        }
        if (source.getRecheckStatus() != CollaborationOfficeConstant.RECHECKSTATUS_HASRECHECK_WAITCHECK) {
            throw new BusinessException(MessageCode.E_RECHECK_STATUS_NO_RECHECKING,
                    ConverterUtils
                            .dynamicArrayToList(CollaborationOfficeConstant.COLLABORATION_OFFICE_RECHECK_STATUS_MAP
                                    .get(source.getRecheckStatus())));
        }
        source.setRecheckExpirationTime(po.getRecheckExpirationTime());// 复核到期时间
        source.setExpectAchieve(po.getExpectAchieve());// 是否达到预期
        source.setCheckOutSign(po.getCheckOutSign() == -1 ? null : po.getCheckOutSign());// 是否逾期 0按时 1逾期 -1表示没有选择
        source.setCloseCode(po.getCloseCode());// 关闭代码
        source.setNoReachReason(po.getNoReachReason());// 未达成原因
        source.setRecheckStatus(CollaborationOfficeConstant.RECHECKSTATUS_HAS_CHECK);
        source.setDescription(po.getDescription());
        collaborationOfficeRecheckDao.update(source);
        // 复核工单为已审，修改变更工单为完成
        List<Long> ticketList = collaborationOfficeRecheckDao.selectTicketIdByChrProcessNumber(source
                .getChrProcessNumber());
        // 安装需求，只关闭一个
        if (!ValidateUtils.isEmptyCollection(ticketList)) {
            changeTicketService.autoDriveWhenSMCCCollaborationOfficeRecheckCompleted(ticketList.get(0),
                    po.getUpdateUserId());
        }
    }

    /**
     * 根据复核单ID查询详细信息
     * 
     * @param reCheckId 复核单ID
     * @return 复核单详细信息
     * @throws BusinessException 业务异常
     */
    @Override
    public CollaborationOfficeRecheckQueryResultVO queryDetailByReCheckId(Long reCheckId) throws BusinessException {
        if (reCheckId == null) {
            throw new BusinessException(MessageCode.E_PARAM_IS_NULL);
        }
        AppCollaborationOfficeRecheck PO = collaborationOfficeRecheckDao.getById(reCheckId);
        if (PO == null) {
            throw new BusinessException(MessageCode.E_COLLABORATION_OFFICE_RECHECK_NOT_EXIST);
        }
        CollaborationOfficeRecheckQueryResultVO vo = new CollaborationOfficeRecheckQueryResultVO();
        BeanUtils.copyProperties(PO, vo);
        List<CollaborationOfficeRecheckQueryResultVO> dataList = Lists.newArrayList(vo);
        this.setTicketAndTaskColumn(dataList);
        return dataList.get(0);
    }

    /**
     * 查找[存在未完成复核单]的工单ID、编号集合
     * 
     * @param ticketIdList 工单ID集合
     * @return 存在未完成复核单的工单ID、编号集合
     * @throws BusinessException 业务异常
     */
    @Override
    public List<IDStringMappingVO> listTheTicketWhoHasUnfinishedRecheckTicket(List<Long> ticketIdList)
            throws BusinessException {
        if (CollectionUtils.isEmpty(ticketIdList)) {
            throw new BusinessException(MessageCode.E_REMOTE_INVOKE_PARAMS_ERROR);
        }
        return collaborationOfficeRecheckDao.listTheTicketWhoHasUnfinishedRecheckTicket(ticketIdList);
    }

    /**
     * 获取有权限的营运组织
     * 
     * @param userType
     * @param userId
     * @param permission
     * @return
     * @throws BusinessException
     */
    private List<Long> getOperationDepartmentIdList(Integer userType, Long userId, Long permission)
            throws BusinessException {
        // 营运组织集合
        List<AppOperationDepartment> operationDepartments = null;
        // 如果人员类型为Admin
        if (OperationUserConstant.ADMIN_TYPE.equals(userType)) {
            // 返回系统所有营运组织
            operationDepartments = operationDepartmentService.getDepartmentsByIds(null);
        } else {
            // 获取为传入权限ID对应分配的营运组织PO集合
            operationDepartments = operationDepartmentService.getOnlyAllocatedDepartmentListByUserIdAndPermissionId(
                    userId, permission);
        }
        List<Long> operationDepartmentIdList = new ArrayList<Long>();
        if (!ValidateUtils.isEmptyCollection(operationDepartments)) {
            // 营运组织ID集合
            for (AppOperationDepartment appOperationDepartment : operationDepartments) {
                operationDepartmentIdList.add(appOperationDepartment.getId());
            }
        }
        return operationDepartmentIdList;
    }

    /**
     * 导出复核单详细信息
     * 
     * @param reCheckIds 工单ID集合
     * @param paramsVO 查询工单参数
     * @throws BusinessException 业务异常
     */
    @Override
    public ExportExcelVO exportCollaborationOfficeRecheckData(CollaborationOfficeRecheckQueryParamsVO paramsVO,
            List<Long> reCheckIds) throws BusinessException {
        PageParameterVo pageParameterVo = new PageParameterVo();
        pageParameterVo.setPageSize(TicketConstant.INCIDENT_TICKET_EXPORT_MAX_VALUE + 1);
        if (!ValidateUtils.isEmptyCollection(reCheckIds)) {
            paramsVO.setReCheckIds(ConverterUtils.listToString(reCheckIds));
        }
        PageDto<CollaborationOfficeRecheckQueryResultVO> result = this.queryCollaborationOfficeRecheckList(paramsVO,
                pageParameterVo);
        if (result != null && result.getTotalCount() > TicketConstant.INCIDENT_TICKET_EXPORT_MAX_VALUE) {
            throw new BusinessException(MessageCode.I_CANNOT_EXPORT_TICKET_COUNT_OVERSIZE,
                    ConverterUtils.dynamicArrayToList("" + result.getTotalCount(), ""
                            + TicketConstant.INCIDENT_TICKET_EXPORT_MAX_VALUE));
        }
        ExportExcel<CollaborationOfficeRecheckExportVO> ex = new ExportExcel<CollaborationOfficeRecheckExportVO>();
        // 表头的字段和顺序
        List<String> headers = Lists.newArrayList("变更编号", "联合办公工单单号", "变更复核状态", "发送复核时间", "复核到期时间", "变更名称", "变更结果",
                "设备或服务停机时间", "复核部门", "园区企业", "是否达到预期", "复核时间", "复核是否逾期", "复核人", "复核说明", "复核类型", "关闭代码", "未达成原因");
        String filename = "变更复核单信息";
        List<CollaborationOfficeRecheckQueryResultVO> source = result.getData();
        List<CollaborationOfficeRecheckExportVO> target = Lists.newArrayList();
        // 转换VO
        this.changeExportDto(source, target);
        // ByteFileVO byteFileVO = ex.exportExcel(filename, headers, target, "yyyy-MM-dd HH:mm:ss", null);
        ExportExcelVO excelVO = new NewExportExcel<CollaborationOfficeRecheckExportVO>().exportExcel(filename, headers,
                target, "yyyy-MM-dd HH:mm:ss", null);
        return excelVO;
    }

    /**
     * 转换成需要导出的对象
     * 
     * @param source 原对象
     * @param target 导出对象
     */
    private void changeExportDto(List<CollaborationOfficeRecheckQueryResultVO> source,
            List<CollaborationOfficeRecheckExportVO> target) {
        for (CollaborationOfficeRecheckQueryResultVO obj : source) {
            CollaborationOfficeRecheckExportVO t = new CollaborationOfficeRecheckExportVO();
            BeanUtils.copyProperties(obj, t, new String[] { "recheckStatus", "expectAchieve", "checkOutSign",
                    "recheckType", "closeCode", "noReachReason" });
            t.setRecheckStatus(CollaborationOfficeConstant.COLLABORATION_OFFICE_RECHECK_STATUS_MAP.get(obj
                    .getRecheckStatus()));
            t.setExpectAchieve(CollaborationOfficeConstant.EXPECT_ACHIEVE_TYPE_MAP.get(obj.getExpectAchieve()));
            t.setCheckOutSign(CollaborationOfficeConstant.CHECK_OUT_MAP.get(obj.getCheckOutSign()));
            t.setRecheckType(CollaborationOfficeConstant.RECHECK_TYPE_MAP.get(obj.getRecheckType()));
            t.setCloseCode(CollaborationOfficeConstant.CLOSE_CODE_MAP.get(obj.getCloseCode()));
            t.setNoReachReason(CollaborationOfficeConstant.NO_REACH_REASON_MAP.get(obj.getNoReachReason()));
            target.add(t);
        }
    }
}
