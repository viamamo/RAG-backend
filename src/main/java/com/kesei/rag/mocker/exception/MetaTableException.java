package com.kesei.rag.mocker.exception;

/**
 * @author kesei
 */
public class MetaTableException extends RuntimeException{
    
    public MetaTableException(String message) {
        super(message);
    }
    
    public MetaTableException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public MetaTableException(Throwable cause) {
        super(cause);
    }
    
    public MetaTableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
