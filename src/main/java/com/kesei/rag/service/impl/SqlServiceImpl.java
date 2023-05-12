package com.kesei.rag.service.impl;

import com.kesei.rag.entity.vo.GenerationVO;
import com.kesei.rag.mocker.GeneratorFacade;
import com.kesei.rag.mocker.entity.MetaTable;
import com.kesei.rag.mocker.entity.MetaTableBuilder;
import com.kesei.rag.mocker.support.dialect.SqlDialect;
import com.kesei.rag.service.SqlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author kesei
 */

@Service
@Slf4j
public class SqlServiceImpl implements SqlService {
    @Override
    public GenerationVO generateBySchema(MetaTable metaTable) {
        return GeneratorFacade.generateAll(metaTable);
    }
    
    @Override
    public MetaTable getSchemaByAuto(String content) {
        return MetaTableBuilder.buildFromAuto(content);
    }
    
    @Override
    public MetaTable getSchemaBySql(String sql, SqlDialect sqlDialect) {
        return MetaTableBuilder.buildFromSql(sql,sqlDialect);
    }
    
    @Override
    public MetaTable getSchemaByExcel(MultipartFile file) {
        return MetaTableBuilder.buildFromExcel(file);
    }
}
