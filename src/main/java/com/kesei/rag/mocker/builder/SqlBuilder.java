package com.kesei.rag.mocker.builder;

import cn.hutool.core.util.StrUtil;
import com.kesei.rag.mocker.support.ResponseCode;
import com.kesei.rag.exception.GenericException;
import com.kesei.rag.mocker.entity.MetaTable;
import com.kesei.rag.mocker.support.FieldType;
import com.kesei.rag.mocker.support.MockType;
import com.kesei.rag.mocker.support.dialect.SqlDialectFactory;
import com.kesei.rag.mocker.support.dialect.impl.MysqlDialect;
import com.kesei.rag.mocker.support.dialect.SqlDialect;
import com.kesei.rag.mocker.support.utils.MockTool;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author kesei
 */
@Slf4j
public class SqlBuilder {
    /**
     * 方言
     */
    public SqlDialect sqlDialect;
    
    public SqlBuilder() {
        this.sqlDialect = SqlDialectFactory.getDialect(MysqlDialect.class.getName());
    }
    
    public SqlBuilder(SqlDialect sqlDialect) {
        this.sqlDialect = sqlDialect;
    }
    
    /**
     * 设置方言
     *
     * @param sqlDialect
     */
    public void setSqlDialect(SqlDialect sqlDialect) {
        this.sqlDialect = sqlDialect;
    }
    
    /**
     * 构造建表 SQL
     *
     * @param metaTable 表概要
     * @return 生成的 SQL
     */
    public String buildCreateTableSql(MetaTable metaTable) {
        // 构造模板
        String template = """
                %s
                create table if not exists %s
                (
                %s
                ) %s;""";
        // 构造表名
        String tableName = sqlDialect.wrapTableName(metaTable.getTableName());
        String dbName = metaTable.getDbName();
        if (StrUtil.isNotBlank(dbName)) {
            tableName = String.format("%s.%s", dbName, tableName);
        }
        // 构造表前缀注释
        String tableComment = metaTable.getTableComment();
        if (StrUtil.isBlank(tableComment)) {
            tableComment = tableName;
        }
        String tablePrefixComment = String.format("-- %s", tableComment);
        // 构造表后缀注释
        String tableSuffixComment = String.format("comment '%s'", tableComment);
        // 构造表字段
        List<MetaTable.MetaField> metaFieldList = metaTable.getMetaFieldList();
        StringBuilder fieldStrBuilder = new StringBuilder();
        int fieldSize = metaFieldList.size();
        for (int i = 0; i < fieldSize; i++) {
            MetaTable.MetaField metaField = metaFieldList.get(i);
            fieldStrBuilder.append(buildCreateFieldSql(metaField));
            // 最后一个字段后没有逗号和换行
            if (i != fieldSize - 1) {
                fieldStrBuilder.append(",");
                fieldStrBuilder.append("\n");
            }
        }
        String fieldStr = fieldStrBuilder.toString();
        // 填充模板
        return String.format(template, tablePrefixComment, tableName, fieldStr, tableSuffixComment);
    }
    
    /**
     * 生成创建字段的 SQL
     *
     * @param metaField
     * @return
     */
    public String buildCreateFieldSql(MetaTable.MetaField metaField) {
        if (metaField == null) {
            throw new GenericException(ResponseCode.PARAMS_ERROR);
        }
        String fieldName = sqlDialect.wrapFieldName(metaField.getFieldName());
        String fieldType = metaField.getFieldType();
        String defaultValue = metaField.getDefaultValue();
        boolean notNull = metaField.isNotNull();
        String comment = metaField.getComment();
        boolean primaryKey = metaField.isPrimaryKey();
        boolean autoIncrement = metaField.isAutoIncrement();
        
        StringBuilder fieldStrBuilder = new StringBuilder();
        // 字段名
        fieldStrBuilder.append(fieldName);
        // 字段类型
        fieldStrBuilder.append(" ").append(fieldType);
        // 默认值
        if (StrUtil.isNotBlank(defaultValue)) {
            fieldStrBuilder.append(" ").append("default ").append(getValueStr(metaField, defaultValue));
        }
        // 是否非空
        fieldStrBuilder.append(" ").append(notNull ? "not null" : "null");
        // 是否自增
        if (autoIncrement) {
            fieldStrBuilder.append(" ").append("auto_increment");
        }
        // 注释
        if (StrUtil.isNotBlank(comment)) {
            fieldStrBuilder.append(" ").append(String.format("comment '%s'", comment));
        }
        // 是否为主键
        if (primaryKey) {
            fieldStrBuilder.append(" ").append("primary key");
        }
        return fieldStrBuilder.toString();
    }
    
    /**
     * 构造插入数据 SQL
     *
     * @param metaTable 表概要
     * @param dataList 数据列表
     * @return 生成的 SQL 列表字符串
     */
    public String buildInsertSql(MetaTable metaTable, List<Map<String, Object>> dataList) {
        // 构造模板
        String template = "insert into %s (%s) values (%s);";
        // 构造表名
        String tableName = sqlDialect.wrapTableName(metaTable.getTableName());
        String dbName = metaTable.getDbName();
        if (StrUtil.isNotBlank(dbName)) {
            tableName = String.format("%s.%s", dbName, tableName);
        }
        // 构造表字段
        List<MetaTable.MetaField> metaFieldList = metaTable.getMetaFieldList();
        // 过滤掉不模拟的字段
        metaFieldList = metaFieldList.stream()
                .filter(field -> {
                    MockType mockType = Optional.ofNullable(MockTool.getMockTypeByValue(field.getMockType()))
                            .orElse(MockType.NONE);
                    return !MockType.NONE.equals(mockType);
                })
                .collect(Collectors.toList());
        StringBuilder resultStringBuilder = new StringBuilder();
        int total = dataList.size();
        for (int i = 0; i < total; i++) {
            Map<String, Object> dataRow = dataList.get(i);
            String keyStr = metaFieldList.stream()
                    .map(field -> sqlDialect.wrapFieldName(field.getFieldName()))
                    .collect(Collectors.joining(", "));
            String valueStr = metaFieldList.stream()
                    .map(field -> getValueStr(field, dataRow.get(field.getFieldName())))
                    .collect(Collectors.joining(", "));
            // 填充模板
            String result = String.format(template, tableName, keyStr, valueStr);
            resultStringBuilder.append(result);
            // 最后一个字段后没有换行
            if (i != total - 1) {
                resultStringBuilder.append("\n");
            }
        }
        return resultStringBuilder.toString();
    }
    
    public List<String> buildInsertSqlList(MetaTable metaTable, List<Map<String, Object>> dataList) {
        List<String> sqlList=new ArrayList<>(dataList.size());
        // 构造模板
        String template = "insert into %s (%s) values (%s);";
        // 构造表名
        String tableName = sqlDialect.wrapTableName(metaTable.getTableName());
        String dbName = metaTable.getDbName();
        if (StrUtil.isNotBlank(dbName)) {
            tableName = String.format("%s.%s", dbName, tableName);
        }
        // 构造表字段
        List<MetaTable.MetaField> metaFieldList = metaTable.getMetaFieldList();
        // 过滤掉不模拟的字段
        metaFieldList = metaFieldList.stream()
                .filter(field -> {
                    MockType mockType = Optional.ofNullable(MockTool.getMockTypeByValue(field.getMockType()))
                            .orElse(MockType.NONE);
                    return !MockType.NONE.equals(mockType);
                })
                .collect(Collectors.toList());
        for (Map<String, Object> dataRow : dataList) {
            String keyStr = metaFieldList.stream()
                    .map(field -> sqlDialect.wrapFieldName(field.getFieldName()))
                    .collect(Collectors.joining(", "));
            String valueStr = metaFieldList.stream()
                    .map(field -> getValueStr(field, dataRow.get(field.getFieldName())))
                    .collect(Collectors.joining(", "));
            // 填充模板
            sqlList.add(String.format(template, tableName, keyStr, valueStr));
        }
        return sqlList;
    }
    
    /**
     * 根据列的属性获取值字符串
     *
     * @param field
     * @param value
     * @return
     */
    public static String getValueStr(MetaTable.MetaField field, Object value) {
        if (field == null || value == null) {
            return "''";
        }
        FieldType fieldType = Optional.ofNullable(MockTool.getFieldTypeByValue(field.getFieldType()))
                .orElse(FieldType.TEXT);
        String result = String.valueOf(value);
        return switch (fieldType) {
            case DATETIME, TIMESTAMP ->
                    StrUtil.isBlank(result)||"CURRENT_TIMESTAMP".equalsIgnoreCase(result) ? "CURRENT_TIMESTAMP" : String.format("'%s'", value);
            case DATE, TIME, CHAR, VARCHAR, TINYTEXT, TEXT, MEDIUMTEXT, LONGTEXT, TINYBLOB, BLOB, MEDIUMBLOB, LONGBLOB, BINARY, VARBINARY ->
                    String.format("'%s'", value);
            default -> result;
        };
    }
}
