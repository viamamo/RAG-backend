package com.kesei.rag.mocker.entity;

import com.kesei.rag.mocker.support.DatabaseType;
import lombok.Data;

import java.util.List;

/**
 * 元表 表信息载体
 *
 * @author kesei
 */
@Data
public class MetaTable {
    
    /**
     * 库名
     */
    private String dbName;
    
    /**
     * 库类型
     */
    private String dbType= DatabaseType.MYSQL.toString();
    
    /**
     * 表名
     */
    private String tableName;
    
    /**
     * 表注释
     */
    private String tableComment;
    
    /**
     * 模拟数据条数
     */
    private Integer mockNum;
    
    /**
     * 元字段 字段信息载体
     */
    private List<MetaField> metaFieldList;
    @Data
    public static class MetaField {
        /**
         * 字段名
         */
        private String fieldName;
        
        /**
         * 字段类型
         */
        private String fieldType;
        
        /**
         * 默认值
         */
        private String defaultValue;
        
        /**
         * 是否非空
         */
        private boolean notNull;
        
        /**
         * 注释（字段中文名）
         */
        private String comment;
        
        /**
         * 是否为主键
         */
        private boolean primaryKey;
        
        /**
         * 是否自增
         */
        private boolean autoIncrement;
        
        /**
         * 模拟类型
         */
        private String mockType;
        
        /**
         * 模拟参数
         */
        private String mockParams;
    }
}
