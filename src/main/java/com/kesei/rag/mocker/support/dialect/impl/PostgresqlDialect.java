package com.kesei.rag.mocker.support.dialect.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGCreateTableParser;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.kesei.rag.entity.po.JobInfo;
import com.kesei.rag.exception.GenericException;
import com.kesei.rag.mocker.entity.MetaTable;
import com.kesei.rag.mocker.support.DatabaseType;
import com.kesei.rag.mocker.support.MockType;
import com.kesei.rag.mocker.support.ResponseCode;
import com.kesei.rag.mocker.support.dialect.SqlDialect;
import com.kesei.rag.mocker.support.dialect.SqlDialectAnnotation;
import com.kesei.rag.mocker.support.utils.MockTool;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Postgresql方言
 * @author viamamo
 */
@SqlDialectAnnotation(databaseType = DatabaseType.POSTGRESQL)
public class PostgresqlDialect implements SqlDialect {
    
    @Override
    public String wrapFieldName(String name) {
        return String.format("\"%s\"", name);
    }
    
    @Override
    public String parseFieldName(String fieldName) {
        if (fieldName.startsWith("\"") && fieldName.endsWith("\"")) {
            return fieldName.substring(1, fieldName.length() - 1);
        }
        return fieldName;
    }
    
    @Override
    public String wrapTableName(String name) {
        return String.format("\"%s\"", name);
    }
    
    @Override
    public String parseTableName(String tableName) {
        if (tableName.startsWith("\"") && tableName.endsWith("\"")) {
            return tableName.substring(1, tableName.length() - 1);
        }
        return tableName;
    }
    
    /**
     * 获取判断列是否存在的SQL
     *
     * @param jobInfo
     * @param columnName
     */
    @Override
    public String getColumnIsExistSql(JobInfo jobInfo, String columnName) {
        String schema = JSONUtil.parseObj(jobInfo.getProperty()).getOrDefault("current_schema", "public").toString();
        return "select column_name from information_schema.columns WHERE table_schema = '"+schema+"' and table_name = '"+jobInfo.getTableName()+"' and column_name = '"+columnName+"';";
    }
    
    /**
     * 构造建表 SQL
     *
     * @param metaTable 表概要
     *
     * @return 生成的 SQL
     */
    @Override
    public String buildCreateTableSql(MetaTable metaTable) {
        // 构造模板
        String template = """
                %s
                create table if not exists %s
                (
                %s
                );
                %s""";
        // 构造表名
        String tableName = wrapTableName(metaTable.getTableName());
        String dbName = metaTable.getDbName();
        if (StrUtil.isNotBlank(dbName)) {
            tableName = String.format("%s.%s", dbName, tableName);
        }
        // 构造表前注释
        String tableComment = metaTable.getTableComment();
        if (StrUtil.isBlank(tableComment)) {
            tableComment = tableName;
        }
        String tablePrefixComment = String.format("-- %s", tableComment);
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
        // 构造注释
        StringBuilder comment = new StringBuilder();
        if(StrUtil.isNotBlank(tableComment)) {
            comment.append(String.format("comment on table %s is '%s';", tableName, tableComment));
        }
        String fieldComments=metaFieldList.stream()
                .filter((field)-> StrUtil.isNotBlank(field.getComment()))
                .map((field)-> String.format("comment on column %s.%s is '%s';", wrapTableName(metaTable.getTableName()), wrapFieldName(field.getFieldName()), field.getComment()))
                .collect(Collectors.joining("\n"));
        if(StrUtil.isNotBlank(fieldComments)){
            comment.append("\n").append(fieldComments);
        }
        // 填充模板
        return String.format(template, tablePrefixComment, tableName, fieldStr, comment);
    }
    
    /**
     * 生成创建字段的 SQL
     *
     * @param metaField
     *
     * @return
     */
    @Override
    public String buildCreateFieldSql(MetaTable.MetaField metaField) {
        if (metaField == null) {
            throw new GenericException(ResponseCode.PARAMS_ERROR);
        }
        String fieldName = wrapFieldName(metaField.getFieldName());
        String fieldType = metaField.getFieldType();
        String defaultValue = metaField.getDefaultValue();
        boolean notNull = metaField.isNotNull();
        boolean primaryKey = metaField.isPrimaryKey();
        boolean autoIncrement = metaField.isAutoIncrement();
    
        StringBuilder fieldStrBuilder = new StringBuilder();
        // 字段名
        fieldStrBuilder.append(fieldName);
        // 字段类型
        // 是否自增
        if (autoIncrement) {
            switch (fieldType){
                case "smallint":fieldStrBuilder.append(" ").append("smallserial");
                case "integer":fieldStrBuilder.append(" ").append("serial");
                case "bigint":fieldStrBuilder.append(" ").append("bigserial");
            }
        }
        else{
            fieldStrBuilder.append(" ").append(fieldType);
            if(Objects.equals(fieldType, "varchar")){
                fieldStrBuilder.append("(255)");
            }
        }
        // 默认值
        if (StrUtil.isNotBlank(defaultValue)) {
            fieldStrBuilder.append(" ").append("default ").append(getValueStr(metaField, defaultValue));
        }
        // 是否非空
        fieldStrBuilder.append(" ").append(notNull ? "not null" : "null");
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
     * @param dataList  数据列表
     *
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
        return new PGCreateTableParser(createTableSql);
    }
}
