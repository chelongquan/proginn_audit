/**
 * 文件名：ParamsCheck.java
 * 上海迈辰信息科技有限公司(http://www.maxeltech.com)
 * Copyright (c) 2012 MAXEL-TECH Corporation
 */
package com.maxeltech.smcc.interceptor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * <li>Description:app端参数Params={}中字符串参数检查注解</li>
 * <li>$Author: chelongquan $</li>
 * <li>$Revision: 68021 $</li>
 * <li>$Date: 2017-05-27 14:43:55 +0800 (Sat, 27 May 2017) $</li>
 * 
 * @version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Documented
public @interface StringValueCheck {

    /**
     * 目标参数
     * 
     * @return
     */
    String name();

    /**
     * 是否必须
     * 
     * @return
     */
    boolean required() default true;

    /**
     * 最大长度
     * 
     * @return
     */
    int maxLength() default -1;
}
