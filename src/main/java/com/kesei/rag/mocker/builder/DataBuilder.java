package com.kesei.rag.mocker.builder;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.kesei.rag.mocker.entity.MetaTable;
import com.kesei.rag.mocker.generator.DataGenerator;
import com.kesei.rag.mocker.generator.DataGeneratorFactory;
import com.kesei.rag.mocker.support.MockType;
import com.kesei.rag.mocker.support.utils.MockTool;
import com.kesei.rag.support.utils.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 数据生成Builder
 *
 * @author viamamo
 */
@Component
@Slf4j
public class DataBuilder {
    
    private DataGeneratorFactory dataGeneratorFactory;
    
    /**
     * 一次性生成数据
     *
     * @param metaTable MetaTable
     * @param rowNum 行数
     *
     * @return 数据结果
     */
    public List<Map<String, Object>> generateData(MetaTable metaTable, int rowNum) {
        if(dataGeneratorFactory==null){
            dataGeneratorFactory=SpringContextUtils.getBean(DataGeneratorFactory.class);
        }
        List<MetaTable.MetaField> fieldList = metaTable.getMetaFieldList();
        // 初始化结果数据
        List<Map<String, Object>> resultList = new ArrayList<>(rowNum);
        for (int i = 0; i < rowNum; i++) {
            resultList.add(new HashMap<>(rowNum));
        }
        // 依次生成每一列
        for (MetaTable.MetaField field : fieldList) {
            MockType mockTypeEnum = Optional.ofNullable(MockTool.getMockTypeByValue(field.getMockType()))
                    .orElse(MockType.NONE);
            DataGenerator dataGenerator = dataGeneratorFactory.getGenerator(mockTypeEnum);
            List<String> mockDataList = dataGenerator.doGenerate(field, rowNum);
            String fieldName = field.getFieldName();
            // 填充结果列表
            if (CollectionUtils.isNotEmpty(mockDataList)) {
                for (int i = 0; i < rowNum; i++) {
                    resultList.get(i).put(fieldName, mockDataList.get(i));
                }
            }
        }
        return resultList;
    }
    
    /**
     * 分块生成数据
     *
     * @param metaTable MetaTable
     * @param blockNumber 块号
     * @param blockSize 块大小
     *
     * @return 数据结果
     */
    public List<Map<String, Object>> generateDataByBlock(MetaTable metaTable, int blockNumber, int blockSize) {
        List<MetaTable.MetaField> fieldList = metaTable.getMetaFieldList();
        // 初始化结果数据
        List<Map<String, Object>> resultList = new ArrayList<>(blockSize);
        for (int i = 0; i < blockSize; i++) {
            resultList.add(new HashMap<>(blockSize));
        }
        // 依次生成每一列
        for (MetaTable.MetaField field : fieldList) {
            MockType mockTypeEnum = Optional.ofNullable(MockTool.getMockTypeByValue(field.getMockType()))
                    .orElse(MockType.NONE);
            DataGenerator dataGenerator = dataGeneratorFactory.getGenerator(mockTypeEnum);
            List<String> mockDataList = dataGenerator.doGenerateBlock(field, blockNumber, blockSize);
            String fieldName = field.getFieldName();
            // 填充结果列表
            if (CollectionUtils.isNotEmpty(mockDataList)) {
                for (int i = 0; i < blockSize; i++) {
                    resultList.get(i).put(fieldName, mockDataList.get(i));
                }
            }
        }
        return resultList;
    }
}
