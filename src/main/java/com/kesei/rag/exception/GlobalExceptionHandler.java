package com.kesei.rag.exception;

import com.kesei.rag.entity.dto.GenericResponse;
import com.kesei.rag.mocker.support.ResponseCode;
import com.kesei.rag.support.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * @author kesei
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(GenericException.class)
    public GenericResponse<?> businessExceptionHandler(GenericException e) {
        log.error("genericException: " + e.getMessage(), e);
        return ResponseUtils.error(e.getCode(), e.getMessage());
    }
    
    @ExceptionHandler(RuntimeException.class)
    public GenericResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("runtimeException", e);
        return ResponseUtils.error(ResponseCode.SYSTEM_ERROR, e.getMessage());
    }
}