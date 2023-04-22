package com.kesei.rag.mocker.generator.impl;

import cn.hutool.core.util.StrUtil;
import com.kesei.rag.mocker.entity.MetaTable;
import com.kesei.rag.mocker.generator.DataGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kesei
 */
public class IncreaseDataGenerator implements DataGenerator {
    @Override
    public List<String> doGenerate(MetaTable.MetaField metaField, int rowNum) {
        String mockParams = StrUtil.blankToDefault(metaField.getMockParams(),"1");
        List<String> list = new ArrayList<>(rowNum);
        int initValue = Integer.parseInt(mockParams);
        for (int i = 0; i < rowNum; i++) {
            list.add(String.valueOf(initValue + i));
        }
        return list;
    }
    
    public List<String> doGenerateBlock(MetaTable.MetaField metaField, int blockNumber, int blockSize) {
        String mockParams = StrUtil.blankToDefault(metaField.getMockParams(),"1");
        List<String> list = new ArrayList<>(blockSize);
        int initValue = Integer.parseInt(mockParams);
        for (int i = 0; i < blockSize; i++) {
            list.add(String.valueOf(initValue+ blockNumber*blockSize + i));
        }
        return list;
    }
}
