package com.kesei.rag.mocker.entity.metadata;

import lombok.Data;

import java.util.List;

/**
 * @author kesei
 */
@Data
public class JavaObjectMetaData {
    
    /**
     * 类名
     */
    private String className;
    
    /**
     * 对象名
     */
    private String objectName;
    
    /**
     * 列信息列表
     */
    private List<MetaField> metaFieldList;
    
    /**
     * 列信息
     */
    @Data
    public static class MetaField {
        /**
         * set 方法名
         */
        private String setMethod;
        
        /**
         * 值
         */
        private String value;
    }
}
