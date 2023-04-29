package com.kesei.rag.service;

import com.kesei.rag.entity.vo.GenerationVO;
import com.kesei.rag.mocker.entity.MetaTable;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author kesei
 */
public interface SqlService {
    GenerationVO generateBySchema(MetaTable metaTable);
    
    MetaTable getSchemaByAuto(String content);
    
    MetaTable getSchemaBySql(String sql);
    
    MetaTable getSchemaByExcel(MultipartFile file);
}
