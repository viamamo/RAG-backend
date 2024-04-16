package com.kesei.rag.mocker.entity.metadata;

import lombok.Data;

import java.util.List;

/**
 * Java实体元数据
 *
 * @author viamamo
 */
@Data
public class JavaEntityMetaData {
    
    /**
     * 类名
     */
    private String className;
    
    /**
     * 类注释
     */
    private String classComment;
    
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
         * 字段名
         */
        private String fieldName;
        
        /**
         * Java 类型
         */
        private String javaType;
        
        /**
         * 注释（字段中文名）
         */
        private String comment;
    }
}
