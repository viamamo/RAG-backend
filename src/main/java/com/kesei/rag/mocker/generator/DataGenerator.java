package com.kesei.rag.mocker.generator;

import com.kesei.rag.mocker.entity.MetaTable;

import java.util.List;

/**
 * 数据生成器接口
 *
 * @author kesei
 */
public interface DataGenerator {
    
    /**
     * 一次性字段数据生成
     *
     * @param metaField 字段信息
     * @param rowNum 行数
     * @return 生成的数据列表
     */
    List<String> doGenerate(MetaTable.MetaField metaField, int rowNum);
    
    /**
     * 分块字段数据生成
     *
     * @param metaField 字段信息
     * @param blockNumber 块号
     * @param blockSize 块大小
     * @return 生成的数据列表
     */
    List<String> doGenerateBlock(MetaTable.MetaField metaField, int blockNumber, int blockSize);
}

