package com.kesei.rag.mocker.support.dialect;

import cn.hutool.core.util.StrUtil;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.SQLParser;
import com.kesei.rag.entity.po.JobInfo;
import com.kesei.rag.mocker.entity.MetaTable;
import com.kesei.rag.mocker.support.FieldType;
import com.kesei.rag.mocker.support.utils.MockTool;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author kesei
 */
public interface SqlDialect {
    
    /**
     * 封装字段名
     * @param name
     * @return
     */
    String wrapFieldName(String name);
    
    /**
     * 解析字段名
     * @param fieldName
     * @return
     */
    String parseFieldName(String fieldName);
    
    /**
     * 封装表名
     * @param name
     * @return
     */
    String wrapTableName(String name);
    
    /**
     * 解析表名
     * @param tableName
     * @return
     */
    String parseTableName(String tableName);
    
    /**
     * 获取判断列是否存在的SQL
     *
     * @param jobInfo
     * @param columnName
     */
    String getColumnIsExistSql(JobInfo jobInfo, String columnName);
    
    /**
     * 构造建表 SQL
     *
     * @param metaTable 表概要
     * @return 生成的 SQL
     */
     String buildCreateTableSql(MetaTable metaTable);
    
    /**
     * 生成创建字段的 SQL
     *
     * @param metaField
     * @return
     */
     String buildCreateFieldSql(MetaTable.MetaField metaField);
    
    /**
     * 构造插入数据 SQL
     *
     * @param metaTable 表概要
     * @param dataList 数据列表
     * @return 生成的 SQL 列表字符串
     */
     String buildInsertSql(MetaTable metaTable, List<Map<String, Object>> dataList);
    
    /**
     * 根据列的属性获取值字符串
     *
     * @param metaField
     * @param value
     * @return
     */
    default String getValueStr(MetaTable.MetaField metaField, Object value) {
        if (metaField == null || value == null) {
            return "''";
        }
        FieldType fieldType = Optional.ofNullable(MockTool.getFieldTypeByValue(metaField.getFieldType()))
                .orElse(FieldType.TEXT);
        String result = String.valueOf(value);
        return switch (fieldType) {
            case TIMESTAMP ->
                    StrUtil.isBlank(result)||"CURRENT_TIMESTAMP".equalsIgnoreCase(result) ? "CURRENT_TIMESTAMP" : String.format("'%s'", value);
            case DATE, TIME, CHAR, VARCHAR, TEXT, BINARY, VARBINARY ->
                    String.format("'%s'", value);
            default -> result;
        };
    }
    
    SQLCreateTableParser getSQLCreateTableParser(String createTableSql);
    
}
