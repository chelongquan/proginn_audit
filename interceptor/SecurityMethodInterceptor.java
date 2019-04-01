package com.maxeltech.smcc.interceptor;

import java.util.List;
import java.util.Set;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.maxeltech.flexframework.dto.PageDto;
import com.maxeltech.flexframework.dto.ResponseDto;
import com.maxeltech.flexframework.vo.Security;
import com.maxeltech.smcc.constant.common.MessageCode;
import com.maxeltech.smcc.utils.common.StringUtils;

public class SecurityMethodInterceptor implements MethodInterceptor {

    /**
     * 日志对象
     */
    private Log log = LogFactory.getLog(getClass());

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // 返回ResponseDto对象
        ResponseDto responseDto = new ResponseDto();
        String requestClassName = invocation.getThis().getClass().getName();
        String requestMethodName = invocation.getMethod().getName();
        if (StringUtils.isEmpty(requestClassName) || StringUtils.isEmpty(requestMethodName)) {
            responseDto.setMessageCode(MessageCode.E_UNKOWN_ERROR);
            log.error("requestClassName  is " + requestClassName + " or requestMethodName is " + requestMethodName);
        } else {
            try {
                // 解密
                Object[] objs = invocation.getArguments();
                if (ArrayUtils.isNotEmpty(objs)) {
                    for (Object obj : objs) {
                        if (obj != null) {
                            if (obj instanceof Security) {
                                ((Security) obj).decrypt();
                            } else if (obj instanceof List && !((List<?>) obj).isEmpty()) {
                                List<?> list = (List<?>) obj;
                                if (list.get(0) instanceof Security) {
                                    for (Object objList : list) {
                                        ((Security) objList).decrypt();
                                    }
                                }
                            } else if (obj instanceof Set && !((Set<?>) obj).isEmpty()) {
                                Set<?> set = (Set<?>) obj;
                                if (set.iterator().next() instanceof Security) {
                                    for (Object objList : set) {
                                        ((Security) objList).decrypt();
                                    }
                                }
                            } else if (obj instanceof PageDto) {
                                List<?> list = ((PageDto<?>) obj).getData();
                                if (CollectionUtils.isNotEmpty(list)) {
                                    if (list.get(0) instanceof Security) {
                                        for (Object objList : list) {
                                            ((Security) objList).decrypt();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                // 注意：invocation.proceed() 表示执行请求，然后获取请求的返回值
                Object object = invocation.proceed();
                if (object != null && !(object instanceof ResponseDto)) {
                    return object;
                } else {
                    responseDto = (ResponseDto) object;
                }
                if (responseDto != null) {
                    Object resultData = responseDto.getData();
                    if (resultData != null && resultData instanceof Security) {
                        ((Security) resultData).encrypt();
                    } else if (resultData instanceof List && !((List<?>) resultData).isEmpty()) {
                        List<?> list = (List<?>) resultData;
                        if (list.get(0) instanceof Security) {
                            for (Object objList : list) {
                                ((Security) objList).encrypt();
                            }
                        }
                    } else if (resultData instanceof Set && !((Set<?>) resultData).isEmpty()) {
                        Set<?> set = (Set<?>) resultData;
                        if (set.iterator().next() instanceof Security) {
                            for (Object objList : set) {
                                ((Security) objList).encrypt();
                            }
                        }
                    } else if (resultData instanceof PageDto) {
                        List<?> list = ((PageDto<?>) resultData).getData();
                        if (CollectionUtils.isNotEmpty(list)) {
                            if (list.get(0) instanceof Security) {
                                for (Object objList : list) {
                                    ((Security) objList).encrypt();
                                }
                            }
                        }
                    }
                }
            } catch (Exception exception) {
                log.error("error message  is " + exception.getMessage(), exception);
                throw exception;
            }
        }
        return responseDto;
    }
}
