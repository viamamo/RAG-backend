package com.kesei.rag.support.utils;

import com.kesei.rag.entity.dto.GenericResponse;
import com.kesei.rag.mocker.support.ResponseCode;

/**
 * @author kesei
 */
public class ResponseUtils {
    public static <T> GenericResponse<T> success(T data){
        return new GenericResponse<>(ResponseCode.SUCCESS,data);
    }
    public static <T> GenericResponse<T> error(ResponseCode responseCode) {
        return new GenericResponse<>(responseCode);
    }
    public static <T> GenericResponse<T> error(ResponseCode responseCode, String message) {
        return new GenericResponse<>(responseCode.getCode(), null, message);
    }
    public static <T> GenericResponse<T> error(int code, String message) {
        return new GenericResponse<>(code, null, message);
    }
}
