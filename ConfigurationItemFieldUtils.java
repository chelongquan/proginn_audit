/**
 * 文件名：ConfigurationItemFieldUtils.java
 * 
 * 上海迈辰信息科技有限公司(http://www.maxeltech.com)
 * Copyright (c) 2014 MAXEL-TECH Corporation
 */
package com.maxeltech.smcc.utils.configurationitem;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.maxeltech.flexframework.utils.TripleDESUtils;
import com.maxeltech.smcc.constant.configurationitem.ConfigurationItemConstant;
import com.maxeltech.smcc.constant.configurationitem.ConfigurationItemDeviceStatusConstant;
import com.maxeltech.smcc.constant.configurationitem.ConfigurationItemFieldDbNameConstant;
import com.maxeltech.smcc.constant.configurationitem.ConfigurationItemFieldSavePretreatmentTypeConstant;
import com.maxeltech.smcc.constant.configurationitem.ConfigurationItemFieldTypeConstant;
import com.maxeltech.smcc.constant.configurationitem.ConfigurationItemSystemTypeConstant;
import com.maxeltech.smcc.constant.configurationitem.ImportanceConstant;
import com.maxeltech.smcc.constant.configurationitem.SecurityRiskAssessmentConstant;
import com.maxeltech.smcc.vo.configurationitem.ConfigurationItemExportFieldVO;
import com.maxeltech.smcc.vo.configurationitem.ConfigurationItemFieldSettingOnModifyVO;
import com.maxeltech.smcc.vo.configurationitem.ConfigurationItemFieldSettingOnViewVO;
import com.maxeltech.smcc.vo.configurationitem.ConfigurationItemSaveFieldVO;
import com.maxeltech.smcc.vo.configurationitem.ConfigurationItemTabVO;
import com.maxeltech.smcc.vo.configurationitem.ConfigurationItemUpdateFieldVO;

/**
 * <p>
 * <li>Description:设备字段工具类</li>
 * <li>$Author: chelongquan $</li>
 * <li>$Revision: 5322 $</li>
 * <li>$Date: 2019-02-22 16:36:31 +0800 (Fri, 22 Feb 2019) $</li>
 * 
 * @version 1.0
 */
public class ConfigurationItemFieldUtils {

    /**
     * 根据风险冲击、威胁可能性计算出风险等级
     * 
     * @param riskShock 风险冲击
     * @param threatLikelihood 威胁可能性
     * @return 风险等级
     */
    public static Integer calcRiskLevel(Integer riskShock, Integer threatLikelihood) {
        Integer riskLevel = null;
        if (null != riskShock && 0 < riskShock && null != threatLikelihood && 0 < threatLikelihood) {
            riskLevel = riskShock * threatLikelihood;
        }
        return riskLevel;
    }

    /**
     * 获取字段的系统预设值数据的显示文本&值键值对
     * <p>
     * <li><b>注意：</b>仅供导入、导出设备功能使用</li>
     * <li>系统内置字段值，是指该字段是由系统提供一组预设值供用户选择</li>
     * <li>该方法返回系统预设值数据的显示文本&值键值对，其中key为显示文本，value为值</li>
     * </p>
     * 
     * @param fieldDbName 字段名
     * @return 值数据的显示文本&值键值对
     * @author xuxie
     */
    public static Map<String, Integer> getSystemDefinedLabelValueMap(String fieldDbName,
            ConfigurationItemExportFieldVO fieldVO) {
        if (StringUtils.isNotBlank(fieldDbName)) {
            // 状态
            if (ConfigurationItemFieldDbNameConstant.DEVICE_STATUS.equals(fieldDbName)) {
                return ConfigurationItemDeviceStatusConstant.DISPLAY_TEXT_AND_VALUE_MAP;
            }
            // 系统类型
            else if (ConfigurationItemFieldDbNameConstant.SYSTEM_TYPE.equals(fieldDbName)) {
                return ConfigurationItemSystemTypeConstant.LABEL_AND_VALUE_MAP;
            }
            // 安全等级
            else if (ConfigurationItemFieldDbNameConstant.SECURITY_LEVEL.equals(fieldDbName)) {
                return SecurityRiskAssessmentConstant.SECURITY_LEVEL_LABEL_AND_VALUE_MAP;
            }
            // 风险冲击
            else if (ConfigurationItemFieldDbNameConstant.RISK_SHOCK.equals(fieldDbName)) {
                return SecurityRiskAssessmentConstant.RISK_SHOCK_LABEL_AND_VALUE_MAP;
            }
            // 威胁可能性
            else if (ConfigurationItemFieldDbNameConstant.THREAT_LIKELIHOOD.equals(fieldDbName)) {
                return SecurityRiskAssessmentConstant.THREAT_LIKELIHOOD_LABEL_AND_VALUE_MAP;
            }
            // 风险等级
            else if (ConfigurationItemFieldDbNameConstant.RISK_LEVEL.equals(fieldDbName)) {
                return SecurityRiskAssessmentConstant.RISK_LEVEL_LABEL_AND_VALUE_MAP;
            }
            // 重要性
            else if (ConfigurationItemFieldDbNameConstant.IMPORTANCE.equals(fieldDbName)) {
                return ImportanceConstant.LABEL_AND_VALUE_MAP;
            }
            // 监控类型
            else if (ConfigurationItemFieldDbNameConstant.MONITOR_TYPE.equals(fieldDbName)) {
                if (ConfigurationItemFieldTypeConstant.VIDEO_MONITOR == fieldVO.getFieldType()) {
                    return ConfigurationItemConstant.MONITOR_TYPE_VIDEO_DISPLAY_TEXT_AND_VALUE_MAP;
                } else if (ConfigurationItemFieldTypeConstant.POLLUTION_SOURCE_MONITOR == fieldVO.getFieldType()) {
                    return ConfigurationItemConstant.MONITOR_TYPE_POLLUTION_SOURCE_DISPLAY_TEXT_AND_VALUE_MAP;
                }
            }
        }
        return null;
    }

    /**
     * 获取字段的系统预设值数据的值&显示文本键值对
     * <p>
     * <li><b>注意：</b>仅供导入、导出设备功能使用</li>
     * <li>系统内置字段值，是指该字段是由系统提供一组预设值供用户选择</li>
     * <li>该方法返回系统预设值数据的值&显示文本键值对，其中key为值，value为显示文本</li>
     * </p>
     * 
     * @param fieldDbName 字段名
     * @return 值数据的值&显示文本键值对
     * @author xuxie
     */
    public static Map<Integer, String> getSystemDefinedValueLabelMap(String fieldDbName,
            ConfigurationItemExportFieldVO fieldVO) {
        if (StringUtils.isNotBlank(fieldDbName)) {
            // 状态
            if (ConfigurationItemFieldDbNameConstant.DEVICE_STATUS.equals(fieldDbName)) {
                return ConfigurationItemDeviceStatusConstant.VALUE_AND_DISPLAY_TEXT_MAP;
            }
            // 系统类型
            else if (ConfigurationItemFieldDbNameConstant.SYSTEM_TYPE.equals(fieldDbName)) {
                return ConfigurationItemSystemTypeConstant.VALUE_AND_LABEL_MAP;
            }
            // 安全等级
            else if (ConfigurationItemFieldDbNameConstant.SECURITY_LEVEL.equals(fieldDbName)) {
                return SecurityRiskAssessmentConstant.SECURITY_LEVEL_VALUE_AND_LABEL_MAP;
            }
            // 风险冲击
            else if (ConfigurationItemFieldDbNameConstant.RISK_SHOCK.equals(fieldDbName)) {
                return SecurityRiskAssessmentConstant.RISK_SHOCK_VALUE_AND_LABEL_MAP;
            }
            // 威胁可能性
            else if (ConfigurationItemFieldDbNameConstant.THREAT_LIKELIHOOD.equals(fieldDbName)) {
                return SecurityRiskAssessmentConstant.THREAT_LIKELIHOOD_VALUE_AND_LABEL_MAP;
            }
            // 风险等级
            else if (ConfigurationItemFieldDbNameConstant.RISK_LEVEL.equals(fieldDbName)) {
                return SecurityRiskAssessmentConstant.RISK_LEVEL_VALUE_AND_LABEL_MAP;
            }
            // 重要性
            else if (ConfigurationItemFieldDbNameConstant.IMPORTANCE.equals(fieldDbName)) {
                return ImportanceConstant.VALUE_AND_LABEL_MAP;
            }
            // 监控类型
            else if (ConfigurationItemFieldDbNameConstant.MONITOR_TYPE.equals(fieldDbName)) {
                if (ConfigurationItemFieldTypeConstant.VIDEO_MONITOR == fieldVO.getFieldType()) {
                    return ConfigurationItemConstant.MONITOR_TYPE_VIDEO_VALUE_AND_DISPLAY_TEXT_MAP;
                } else if (ConfigurationItemFieldTypeConstant.POLLUTION_SOURCE_MONITOR == fieldVO.getFieldType()) {
                    return ConfigurationItemConstant.MONITOR_TYPE_POLLUTION_SOURCE_VALUE_AND_DISPLAY_TEXT_MAP;
                }
            }
        }
        return null;
    }

    /**
     * 数据加密（在查看、编辑设备前调用）
     * 
     * @param tabVOList 选项卡VO集合
     */
    public static void encrypt(List<ConfigurationItemTabVO> tabVOList) {
        if (CollectionUtils.isNotEmpty(tabVOList)) {
            for (ConfigurationItemTabVO tabVO : tabVOList) {
                if (CollectionUtils.isNotEmpty(tabVO.getFieldVOList())) {
                    for (Object fieldVO : tabVO.getFieldVOList()) {
                        if (fieldVO instanceof ConfigurationItemFieldSettingOnViewVO) {
                            ConfigurationItemFieldSettingOnViewVO viewFieldVO = (ConfigurationItemFieldSettingOnViewVO) fieldVO;
                            if (ConfigurationItemFieldDbNameConstant.ENCRYPTED_TRANSMISSION_FIELD_DB_NAMES
                                    .contains(viewFieldVO.getFieldDbName()) && null != viewFieldVO.getFieldValue()) {
                                viewFieldVO.setFieldValue(TripleDESUtils
                                        .encrypt(viewFieldVO.getFieldValue().toString()));
                            }
                        } else if (fieldVO instanceof ConfigurationItemFieldSettingOnModifyVO) {
                            ConfigurationItemFieldSettingOnModifyVO modifyFieldVO = (ConfigurationItemFieldSettingOnModifyVO) fieldVO;
                            if (ConfigurationItemFieldDbNameConstant.ENCRYPTED_TRANSMISSION_FIELD_DB_NAMES
                                    .contains(modifyFieldVO.getFieldDbName()) && null != modifyFieldVO.getFieldValue()) {
                                modifyFieldVO.setFieldValue(TripleDESUtils.encrypt(modifyFieldVO.getFieldValue()
                                        .toString()));
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * 数据解密（在执行创建设备前调用）
     * 
     * @param beSavedFieldVO 待保存字段数据
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void decryptBeforeCreate(ConfigurationItemSaveFieldVO beSavedFieldVO) {
        if (null != beSavedFieldVO && null != beSavedFieldVO.getFieldValue()) {
            // 普通字段
            if (ConfigurationItemFieldDbNameConstant.ENCRYPTED_TRANSMISSION_FIELD_DB_NAMES.contains(beSavedFieldVO
                    .getFieldDbName())) {
                beSavedFieldVO.setFieldValue(TripleDESUtils.decrypt(beSavedFieldVO.getFieldValue().toString()));
            }
            // 运维告警
            else if (beSavedFieldVO.getSavePretreatmentType() != null
                    && ConfigurationItemFieldSavePretreatmentTypeConstant.MAINTENANCE_ALARM == beSavedFieldVO
                            .getSavePretreatmentType()) {
                if (null != beSavedFieldVO.getFieldValue() && (beSavedFieldVO.getFieldValue() instanceof Collection)) {
                    TripleDESUtils.decrypt((Collection) beSavedFieldVO.getFieldValue());
                }
            }
        }
    }

    /**
     * 数据加密（在执行更新设备前调用）
     * 
     * @param beUpdatedFieldVO 待更新字段数据
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void decryptBeforeUpdate(ConfigurationItemUpdateFieldVO beUpdatedFieldVO) {
        if (null != beUpdatedFieldVO && null != beUpdatedFieldVO.getFieldValue()) {
            // 普通字段
            if (ConfigurationItemFieldDbNameConstant.ENCRYPTED_TRANSMISSION_FIELD_DB_NAMES.contains(beUpdatedFieldVO
                    .getFieldDbName())) {
                beUpdatedFieldVO.setFieldValue(TripleDESUtils.decrypt(beUpdatedFieldVO.getFieldValue().toString()));
            }
            // 运维告警
            else if (beUpdatedFieldVO.getSavePretreatmentType() != null
                    && ConfigurationItemFieldSavePretreatmentTypeConstant.MAINTENANCE_ALARM == beUpdatedFieldVO
                            .getSavePretreatmentType()) {
                if (null != beUpdatedFieldVO.getFieldValue()
                        && (beUpdatedFieldVO.getFieldValue() instanceof Collection)) {
                    TripleDESUtils.decrypt((Collection) beUpdatedFieldVO.getFieldValue());
                }
            }
        }
    }
}
