package com.kesei.rag.mocker.generator.impl;

import cn.hutool.json.JSONUtil;
import com.kesei.rag.entity.po.DictInfo;
import com.kesei.rag.exception.GenericException;
import com.kesei.rag.mocker.entity.MetaTable;
import com.kesei.rag.mocker.generator.DataGenerator;
import com.kesei.rag.mocker.generator.DataGeneratorAnnotation;
import com.kesei.rag.mocker.support.MockType;
import com.kesei.rag.mocker.support.ResponseCode;
import com.kesei.rag.service.DictInfoService;
import com.kesei.rag.support.utils.SpringContextUtils;
import org.apache.commons.lang3.RandomUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 词库数据生成器
 *
 * @author viamamo
 */
@DataGeneratorAnnotation(mockType = MockType.DICT)
public class DictDataGenerator implements DataGenerator {
    
    private DictInfoService dictInfoService=null;
    
    @Override
    public List<String> doGenerate(MetaTable.MetaField metaField, int rowNum) {
        if(Objects.isNull(dictInfoService)){
            dictInfoService=SpringContextUtils.getBean(DictInfoService.class);
        }
        String mockParams = metaField.getMockParams();
        long id = Long.parseLong(mockParams);
        DictInfo dictInfo = dictInfoService.getById(id);
        if (dictInfo == null) {
            throw new GenericException(ResponseCode.NOT_FOUND_ERROR, "字段"+metaField.getFieldName()+"词库不存在");
        }
        List<String> wordList = JSONUtil.parseArray(dictInfo.getContent()).toList(String.class);
        List<String> list = new ArrayList<>(rowNum);
        for (int i = 0; i < rowNum; i++) {
            String randomStr = wordList.get(RandomUtils.nextInt(0, wordList.size()));
            list.add(randomStr);
        }
        return list;
    }
    
    @Override
    public List<String> doGenerateBlock(MetaTable.MetaField metaField, int blockNumber, int blockSize) {
        if(Objects.isNull(dictInfoService)){
            dictInfoService=SpringContextUtils.getBean(DictInfoService.class);
        }
        String mockParams = metaField.getMockParams();
        long id = Long.parseLong(mockParams);
        DictInfo dictInfo = dictInfoService.getById(id);
        if (dictInfo == null) {
            throw new GenericException(ResponseCode.NOT_FOUND_ERROR, "字段"+metaField.getFieldName()+"词库不存在");
        }
        List<String> wordList = JSONUtil.parseArray(dictInfo.getContent()).toList(String.class);
        List<String> list = new ArrayList<>(blockSize);
        for (int i = 0; i < blockSize; i++) {
            String randomStr = wordList.get(RandomUtils.nextInt(0, wordList.size()));
            list.add(randomStr);
        }
        return list;
    }
}
