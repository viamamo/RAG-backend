package com.kesei.rag.mocker.support;

import com.kesei.rag.support.Constants;

/**
 * 响应码封装
 * @author kesei
 */
public enum ResponseCode {
    
    /**
     * success 成功
     * PARAMS_ERROR 请求参数错误
     * UNAUTHORIZED_ERROR 未登录/权限不足
     * NOT_FOUND_ERROR 请求数据不存在
     * SYSTEM_ERROR 系统内部异常
     * SQL_OPERATION_ERROR 数据库操作失败
     */
    SUCCESS(Constants.SUCCESS_CODE, Constants.SUCCESS_MESSAGE),
    PARAMS_ERROR(Constants.PARAMS_ERROR_CODE, Constants.PARAMS_ERROR_MESSAGE),
    UNAUTHORIZED_ERROR(Constants.UNAUTHORIZED_ERROR_CODE, Constants.UNAUTHORIZED_ERROR_MESSAGE),
    NOT_FOUND_ERROR(Constants.NOT_FOUND_ERROR_CODE, Constants.NOT_FOUND_ERROR_MESSAGE),
    SYSTEM_ERROR(Constants.SYSTEM_ERROR_CODE, Constants.SYSTEM_ERROR_MESSAGE),
    SQL_OPERATION_ERROR(Constants.SQL_OPERATION_ERROR_CODE, Constants.SQL_OPERATION_ERROR_MESSAGE);
    
    /**
     * 错误码
     */
    private final int code;
    /**
     * 错误信息
     */
    private final String message;
    
    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
    
}