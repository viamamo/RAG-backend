package com.kesei.rag.mocker.support.dialect.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlCreateTableParser;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.SQLParser;
import com.kesei.rag.entity.po.JobInfo;
import com.kesei.rag.exception.GenericException;
import com.kesei.rag.mocker.entity.MetaTable;
import com.kesei.rag.mocker.support.DatabaseType;
import com.kesei.rag.mocker.support.MockType;
import com.kesei.rag.mocker.support.ResponseCode;
import com.kesei.rag.mocker.support.dialect.SqlDialect;
import com.kesei.rag.mocker.support.dialect.SqlDialectAnnotation;
import com.kesei.rag.mocker.support.utils.MockTool;
import com.kesei.rag.support.Constants;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * MySQL方言
 *
 * @author kesei
 */
@SqlDialectAnnotation(databaseType = DatabaseType.MYSQL)
public class MysqlDialect implements SqlDialect {
    
    @Override
    public String wrapFieldName(String name) {
        return String.format("`%s`", name);
    }
    
    @Override
    public String parseFieldName(String fieldName) {
        if (fieldName.startsWith(Constants.MYSQL_ESCAPE_CHARACTER) && fieldName.endsWith(Constants.MYSQL_ESCAPE_CHARACTER)) {
            return fieldName.substring(1, fieldName.length() - 1);
        }
        return fieldName;
    }
    
    @Override
    public String wrapTableName(String name) {
        return String.format("`%s`", name);
    }
    
    @Override
    public String parseTableName(String tableName) {
        if (tableName.startsWith(Constants.MYSQL_ESCAPE_CHARACTER) && tableName.endsWith(Constants.MYSQL_ESCAPE_CHARACTER)) {
            return tableName.substring(1, tableName.length() - 1);
        }
        return tableName;
    }
    
    @Override
    public String getColumnIsExistSql(JobInfo jobInfo, String columnName) {
        return "SELECT `COLUMN_NAME` FROM `INFORMATION_SCHEMA`.`COLUMNS` WHERE `TABLE_SCHEMA`='"+jobInfo.getDbName()+"' AND `TABLE_NAME`='"+jobInfo.getTableName()+"' AND `COLUMN_NAME`='"+columnName+"'";
    }
    
    @Override
    public String buildCreateTableSql(MetaTable metaTable) {
        // 构造模板
        String template = """
                %s
                create table if not exists %s
                (
                %s
                ) %s;""";
        // 构造表名
        String tableName = wrapTableName(metaTable.getTableName());
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
    
    @Override
    public String buildCreateFieldSql(MetaTable.MetaField metaField) {
        if (metaField == null) {
            throw new GenericException(ResponseCode.PARAMS_ERROR);
        }
        String fieldName = wrapFieldName(metaField.getFieldName());
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
        if(Objects.equals(fieldType, "varchar")){
            fieldStrBuilder.append("(255)");
        }
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
    @Override
    public String buildInsertSql(MetaTable metaTable, List<Map<String, Object>> dataList) {
        // 构造模板
        String template = "insert into %s (%s) values (%s);";
        // 构造表名
        String tableName = wrapTableName(metaTable.getTableName());
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
                    return !MockType.NONE.equals(mockType)&&!field.isAutoIncrement();
                })
                .collect(Collectors.toList());
        StringBuilder resultStringBuilder = new StringBuilder();
        int total = dataList.size();
        for (int i = 0; i < total; i++) {
            Map<String, Object> dataRow = dataList.get(i);
            String keyStr = metaFieldList.stream()
                    .map(field -> wrapFieldName(field.getFieldName()))
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
    
    @Override
    public SQLCreateTableParser getSQLCreateTableParser(String createTableSql) {
        return new MySqlCreateTableParser(createTableSql);
    }
}
