package com.kesei.rag.service;

import com.kesei.rag.entity.vo.GenerationVO;
import com.kesei.rag.mocker.entity.MetaTable;
import com.kesei.rag.mocker.support.dialect.SqlDialect;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author kesei
 */
public interface SqlService {
    
    /**
     * 生成
     *
     * @param metaTable MetaTable
     * @return 生成结果
     */
    GenerationVO generateBySchema(MetaTable metaTable);
    
    /**
     * 根据语义自动获取 MetaTable
     *
     * @param content 逗号分隔的字段名
     * @return 解析完成的MetaTable
     */
    MetaTable getSchemaByAuto(String content);
    
    
    /**
     * 根据建表 SQL 构建
     *
     * @param sql 建表 SQL
     * @param sqlDialect 数据库方言
     * @return 解析完成的MetaTable
     */
    MetaTable getSchemaBySql(String sql, SqlDialect sqlDialect);
    
    /**
     * 根据 Excel 文件构建
     *
     * @param file Excel 文件
     * @return 解析完成的MetaTable
     */
    MetaTable getSchemaByExcel(MultipartFile file);
}
