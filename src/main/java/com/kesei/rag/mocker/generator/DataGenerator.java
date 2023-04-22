package com.kesei.rag.mocker.generator;

import com.kesei.rag.mocker.entity.MetaTable;

import java.util.List;

/**
 * @author kesei
 */
public interface DataGenerator {
    
    /**
     * 生成
     *
     * @param metaField 字段信息
     * @param rowNum 行数
     * @return 生成的数据列表
     */
    List<String> doGenerate(MetaTable.MetaField metaField, int rowNum);
    
    List<String> doGenerateBlock(MetaTable.MetaField metaField, int blockNumber, int blockSize);
}

