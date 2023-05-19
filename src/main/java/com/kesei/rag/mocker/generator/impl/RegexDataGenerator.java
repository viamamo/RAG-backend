package com.kesei.rag.mocker.generator.impl;

import com.kesei.rag.mocker.entity.MetaTable;
import com.kesei.rag.mocker.generator.DataGenerator;
import com.kesei.rag.mocker.generator.DataGeneratorAnnotation;
import com.kesei.rag.mocker.support.MockType;
import com.mifmif.common.regex.Generex;

import java.util.ArrayList;
import java.util.List;

/**
 * 正则数据生成器
 *
 * @author kesei
 */
@DataGeneratorAnnotation(mockType = MockType.REGEX)
public class RegexDataGenerator implements DataGenerator {
    @Override
    public List<String> doGenerate(MetaTable.MetaField metaField, int rowNum) {
        String mockParams = metaField.getMockParams();
        List<String> list = new ArrayList<>(rowNum);
        Generex generex = new Generex(mockParams);
        for (int i = 0; i < rowNum; i++) {
            String randomStr = generex.random();
            list.add(randomStr);
        }
        return list;
    }
    
    @Override
    public List<String> doGenerateBlock(MetaTable.MetaField metaField, int blockNumber, int blockSize) {
        String mockParams = metaField.getMockParams();
        List<String> list = new ArrayList<>(blockSize);
        Generex generex = new Generex(mockParams);
        for (int i = 0; i < blockSize; i++) {
            String randomStr = generex.random();
            list.add(randomStr);
        }
        return list;
    }
}
