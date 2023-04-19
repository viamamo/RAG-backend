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
     * @param field 字段信息
     * @param rowNum 行数
     * @return 生成的数据列表
     */
    List<String> doGenerate(MetaTable.MetaField field, int rowNum);
    
}

