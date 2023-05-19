package com.kesei.rag.entity.dto;

import com.kesei.rag.mocker.support.ResponseCode;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 通用响应封装
 *
 * @author kesei
 */
@Data
public class GenericResponse<T> implements Serializable {
    
    @Serial
    private static final long serialVersionUID = 1L;
    
    private int code;
    private T data;
    private String message;
    
    public GenericResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }
    
    public GenericResponse(ResponseCode responseCode,T data){
        this(responseCode.getCode(), data, responseCode.getMessage());
    }
    
    public GenericResponse(ResponseCode responseCode) {
        this(responseCode.getCode(), null, responseCode.getMessage());
    }
}
