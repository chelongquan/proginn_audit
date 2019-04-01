/**
 * 文件名：ConfigurationItemReflectUtils.java
 * 
 * 上海迈辰信息科技有限公司(http://www.maxeltech.com)
 * Copyright (c) 2012 MAXEL-TECH Corporation
 */
package com.maxeltech.smcc.utils.configurationitem;

import java.lang.reflect.Method;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.maxeltech.smcc.po.AppConfigurationItem;
import com.maxeltech.smcc.utils.common.DateStringCalcUtils;

/**
 * <p>
 * <li>Description:设备反射工具类</li>
 * <li>$Author: chelongquan $</li>
 * <li>$Revision: 5156 $</li>
 * <li>$Date: 2019-02-14 10:05:12 +0800 (Thu, 14 Feb 2019) $</li>
 * 
 * @version 1.0
 */
@SuppressWarnings("all")
public class ConfigurationItemReflectUtils {

    /**
     * 日志对象
     */
    private static final Logger log = Logger.getLogger(ConfigurationItemReflectUtils.class);

    /**
     * 将设备数据库字段名转换成写入器方法名
     * 
     * @param fieldDbName 设备数据库字段名
     * @return 写入器方法名
     */
    public static String parseToSetterFunctionName(String fieldDbName) {
        if (StringUtils.isBlank(fieldDbName)) {
            return null;
        }
        String functionName = "";
        String[] tmpStringArray = fieldDbName.split("_");
        functionName += "set";
        for (String tmpString : tmpStringArray) {
            if (StringUtils.isBlank(tmpString)) {
                continue;
            }
            String firstChar = tmpString.substring(0, 1);
            String elseChar = tmpString.substring(1, tmpString.length());
            firstChar = firstChar.toUpperCase();
            functionName += (firstChar + elseChar);
        }
        return functionName;
    }

    /**
     * 将设备数据库字段名转换成读取器方法名
     * 
     * @param fieldDbName 设备数据库字段名
     * @return 写入器方法名
     */
    public static String parseToGetterFunctionName(String fieldDbName) {
        if (StringUtils.isBlank(fieldDbName)) {
            return null;
        }
        String functionName = "";
        String[] tmpStringArray = fieldDbName.split("_");
        functionName += "get";
        for (String tmpString : tmpStringArray) {
            if (StringUtils.isBlank(tmpString)) {
                continue;
            }
            String firstChar = tmpString.substring(0, 1);
            String elseChar = tmpString.substring(1, tmpString.length());
            firstChar = firstChar.toUpperCase();
            functionName += (firstChar + elseChar);
        }
        return functionName;
    }

    /**
     * 获得字符串类型值
     * 
     * @param configurationItem 设备实体
     * @param fieldDbName 设备数据库名
     * @return 字符串类型值
     */
    public static String getStringValue(AppConfigurationItem configurationItem, String fieldDbName) {
        String value = "";
        try {
            String getter = parseToGetterFunctionName(fieldDbName);
            Class cls = configurationItem.getClass();
            Method method = cls.getMethod(getter);
            if (null != method) {
                Class clazz = method.getReturnType();
                Object tmpValue = method.invoke(configurationItem);
                if (null != tmpValue) {
                    if (clazz == Date.class) {
                        value = DateStringCalcUtils.dateToStringToGeneralYMDHMS((Date) tmpValue);
                    } else {
                        value = tmpValue.toString();
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return value;
    }

    /**
     * 执行某对象的方法
     * 
     * @param target 目标对象
     * @param fieldDbName 设备字段名
     * @param arg 参数
     * @throws Exception 异常
     */
    public static void setValue(Object target, String fieldDbName, Object arg) throws Exception {
        Class clazz = null;
        String getter = parseToGetterFunctionName(fieldDbName);
        String setter = parseToSetterFunctionName(fieldDbName);
        Class cls = target.getClass();
        Method method = cls.getMethod(getter);
        clazz = method.getReturnType();
        method = cls.getMethod(setter, clazz);
        Object castedArg = null;
        if (null != arg && StringUtils.isNotBlank(arg.toString())) {
            if (clazz == Integer.class) {
                castedArg = arg == null ? null : Integer.parseInt(arg.toString());
            } else if (clazz == Long.class) {
                castedArg = arg == null ? null : Long.parseLong(arg.toString());
            } else if (clazz == Double.class) {
                castedArg = arg == null ? null : Double.parseDouble(arg.toString());
            } else if (clazz == Short.class) {
                castedArg = arg == null ? null : Short.parseShort(arg.toString());
            } else {
                castedArg = arg == null ? null : clazz.cast(arg);
            }
        }
        method.invoke(target, castedArg);
    }

    /**
     * 复制字段值（将源对象的字段值赋予目标对象）
     * 
     * @param source 源对象
     * @param target 目标对象
     * @param fieldDbName 设备字段名
     * @throws Exception 异常
     */
    public static void copyValue(Object source, Object target, String fieldDbName) throws Exception {
        String getter = parseToGetterFunctionName(fieldDbName);
        String setter = parseToSetterFunctionName(fieldDbName);
        Class targetClazz = target.getClass();
        Method getterMethod = targetClazz.getMethod(getter);
        Method setterMethod = targetClazz.getMethod(setter, getterMethod.getReturnType());
        setterMethod.invoke(target, getValue(source, fieldDbName));
    }

    /**
     * 对比字段值（如果源对象与目标对象的字段值相同，则返回false，否则返回true）
     * 
     * @param source 源对象
     * @param target 目标对象
     * @param fieldDbName 设备字段名
     * @throws Exception 异常
     */
    public static boolean compareValue(Object source, Object target, String fieldDbName) throws Exception {
        Class targetClazz = target.getClass();
        String getter = parseToGetterFunctionName(fieldDbName);
        Method getterMethod = targetClazz.getMethod(getter);
        Object sourceFieldValue = getterMethod.invoke(source);
        String sourceFieldStringValue = "";
        if (null != sourceFieldValue) {
            if (getterMethod.getReturnType() == Date.class) {
                sourceFieldStringValue = DateStringCalcUtils.dateToStringToGeneralYMDHMS((Date) sourceFieldValue);
            } else {
                sourceFieldStringValue = sourceFieldValue.toString();
            }
        }
        Object targetFieldValue = getterMethod.invoke(target);
        String targetFieldStringValue = "";
        if (null != targetFieldValue) {
            if (getterMethod.getReturnType() == Date.class) {
                targetFieldStringValue = DateStringCalcUtils.dateToStringToGeneralYMDHMS((Date) targetFieldValue);
            } else {
                targetFieldStringValue = targetFieldValue.toString();
            }
        }
        return targetFieldStringValue.trim().equals(sourceFieldStringValue.trim());
    }

    /**
     * 根据属性名获得目标对象的属性值
     * 
     * @param target 目标对象
     * @param fieldDbName 设备数据库字段名
     * @return 字符串类型值
     */
    public static Object getValue(Object target, String fieldDbName) {
        Object value = null;
        try {
            String getter = parseToGetterFunctionName(fieldDbName);
            Class cls = target.getClass();
            Method method = cls.getMethod(getter);
            if (null != method) {
                Class clazz = method.getReturnType();
                value = method.invoke(target);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return value;
    }
}
