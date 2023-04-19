package com.kesei.rag.exception;

import com.kesei.rag.mocker.support.ResponseCode;

/**
 * @author kesei
 */
public class GenericException extends RuntimeException{
    
    private final int code;
    
    public GenericException(int code, String message) {
        super(message);
        this.code = code;
    }
    
    public GenericException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.code = responseCode.getCode();
    }
    
    public GenericException(ResponseCode responseCode, String message) {
        super(message);
        this.code = responseCode.getCode();
    }
    
    public int getCode() {
        return code;
    }
}
