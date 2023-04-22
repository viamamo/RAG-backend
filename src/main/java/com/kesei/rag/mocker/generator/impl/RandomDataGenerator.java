package com.kesei.rag.mocker.generator.impl;

import com.kesei.rag.mocker.entity.MetaTable;
import com.kesei.rag.mocker.generator.DataGenerator;
import com.kesei.rag.mocker.support.FakerType;
import com.kesei.rag.mocker.support.utils.FakerUtils;
import com.kesei.rag.mocker.support.utils.MockTool;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author kesei
 */
public class RandomDataGenerator implements DataGenerator {
    @Override
    public List<String> doGenerate(MetaTable.MetaField metaField, int rowNum) {
        String mockParams = metaField.getMockParams();
        List<String> list = new ArrayList<>(rowNum);
        for (int i = 0; i < rowNum; i++) {
            FakerType fakerType = Optional.ofNullable(
                            MockTool.getFakerTypeByValue(mockParams))
                    .orElse(FakerType.STRING);
            String randomString = FakerUtils.getRandomValue(fakerType);
            list.add(randomString);
        }
        return list;
    }
    
    @Override
    public List<String> doGenerateBlock(MetaTable.MetaField metaField, int blockNumber, int blockSize) {
        String mockParams = metaField.getMockParams();
        List<String> list = new ArrayList<>(blockSize);
        for (int i = 0; i < blockSize; i++) {
            FakerType fakerType = Optional.ofNullable(
                            MockTool.getFakerTypeByValue(mockParams))
                    .orElse(FakerType.STRING);
            String randomString = FakerUtils.getRandomValue(fakerType);
            list.add(randomString);
        }
        return list;
    }
}
