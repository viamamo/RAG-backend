package com.kesei.rag.mocker.builder;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.kesei.rag.mocker.entity.MetaTable;
import com.kesei.rag.mocker.generator.DataGenerator;
import com.kesei.rag.mocker.generator.DataGeneratorFactory;
import com.kesei.rag.mocker.support.MockType;
import com.kesei.rag.mocker.support.utils.MockTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author kesei
 */
@Component
@Slf4j
public class DataBuilder {
    
    /**
     * 生成数据
     *
     * @param metaTable
     * @param rowNum
     * @return
     */
    public List<Map<String, Object>> generateData(MetaTable metaTable, int rowNum) {
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
            DataGenerator dataGenerator = DataGeneratorFactory.getGenerator(mockTypeEnum);
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
}
