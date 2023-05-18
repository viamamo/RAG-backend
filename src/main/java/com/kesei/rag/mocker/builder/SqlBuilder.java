package com.kesei.rag.mocker.builder;

import cn.hutool.core.util.StrUtil;
import com.kesei.rag.exception.GenericException;
import com.kesei.rag.mocker.entity.MetaTable;
import com.kesei.rag.mocker.support.FieldType;
import com.kesei.rag.mocker.support.MockType;
import com.kesei.rag.mocker.support.ResponseCode;
import com.kesei.rag.mocker.support.dialect.SqlDialect;
import com.kesei.rag.mocker.support.utils.MockTool;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    
    public SqlBuilder(SqlDialect sqlDialect) {
        this.sqlDialect = sqlDialect;
    }
    
    
    /**
     * 构造建表 SQL
     *
     * @param metaTable 表概要
     * @return 生成的 SQL
     */
    public String buildCreateTableSql(MetaTable metaTable) {
        return sqlDialect.buildCreateTableSql(metaTable);
    }
    
    /**
     * 构造插入数据 SQL
     *
     * @param metaTable 表概要
     * @param dataList 数据列表
     * @return 生成的 SQL 列表字符串
     */
    public String buildInsertSql(MetaTable metaTable, List<Map<String, Object>> dataList) {
        return sqlDialect.buildInsertSql(metaTable, dataList);
    }
}
