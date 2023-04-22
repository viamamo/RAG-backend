package com.kesei.rag.mocker.generator.impl;

import cn.hutool.core.util.StrUtil;
import com.kesei.rag.mocker.entity.MetaTable;
import com.kesei.rag.mocker.generator.DataGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kesei
 */
public class FixedDataGenerator implements DataGenerator {
    @Override
    public List<String> doGenerate(MetaTable.MetaField metaField, int rowNum) {
        String mockParams = StrUtil.blankToDefault(metaField.getMockParams(),"1");
        List<String> list = new ArrayList<>(rowNum);
        for (int i = 0; i < rowNum; i++) {
            list.add(mockParams);
        }
        return list;
    }
    
    @Override
    public List<String> doGenerateBlock(MetaTable.MetaField metaField, int blockNumber, int blockSize) {
        String mockParams = StrUtil.blankToDefault(metaField.getMockParams(),"1");
        List<String> list = new ArrayList<>(blockSize);
        for (int i = 0; i < blockSize; i++) {
            list.add(mockParams);
        }
        return list;
    }
}
