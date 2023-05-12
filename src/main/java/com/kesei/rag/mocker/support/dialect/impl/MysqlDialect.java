package com.kesei.rag.mocker.support.dialect.impl;

import com.kesei.rag.mocker.support.DatabaseType;
import com.kesei.rag.mocker.support.dialect.SqlDialect;
import com.kesei.rag.mocker.support.dialect.SqlDialectAnnotation;
import com.kesei.rag.support.Constants;

/**
 * @author kesei
 */
@SqlDialectAnnotation(databaseType = DatabaseType.MYSQL)
public class MysqlDialect implements SqlDialect {
    
    /**
     * 封装字段名
     *
     * @param name
     * @return
     */
    @Override
    public String wrapFieldName(String name) {
        return String.format("`%s`", name);
    }
    
    /**
     * 解析字段名
     *
     * @param fieldName
     * @return
     */
    @Override
    public String parseFieldName(String fieldName) {
        if (fieldName.startsWith(Constants.MYSQL_ESCAPE_CHARACTER) && fieldName.endsWith(Constants.MYSQL_ESCAPE_CHARACTER)) {
            return fieldName.substring(1, fieldName.length() - 1);
        }
        return fieldName;
    }
    
    /**
     * 包装表名
     *
     * @param name
     * @return
     */
    @Override
    public String wrapTableName(String name) {
        return String.format("`%s`", name);
    }
    
    /**
     * 解析表名
     *
     * @param tableName
     * @return
     */
    @Override
    public String parseTableName(String tableName) {
        if (tableName.startsWith(Constants.MYSQL_ESCAPE_CHARACTER) && tableName.endsWith(Constants.MYSQL_ESCAPE_CHARACTER)) {
            return tableName.substring(1, tableName.length() - 1);
        }
        return tableName;
    }
    
    /**
     * 获取判断列是否存在的SQL
     *
     * @param dbName
     * @param tableName
     * @param columnName
     */
    @Override
    public String getColumnIsExistSql(String dbName, String tableName, String columnName) {
        return "SELECT `COLUMN_NAME` FROM `INFORMATION_SCHEMA`.`COLUMNS` WHERE `TABLE_SCHEMA`='"+dbName+"' AND `TABLE_NAME`='"+tableName+"' AND `COLUMN_NAME`='"+columnName+"'";
    }
}
