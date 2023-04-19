package com.kesei.rag.mocker.generator.impl;

import cn.hutool.core.date.DateUtil;
import com.kesei.rag.mocker.entity.MetaTable;
import com.kesei.rag.mocker.generator.DataGenerator;
import com.kesei.rag.support.Constants;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author kesei
 */
public class DefaultDataGenerator implements DataGenerator {
    @Override
    public List<String> doGenerate(MetaTable.MetaField field, int rowNum) {
        String mockParams = field.getMockParams();
        List<String> list = new ArrayList<>(rowNum);
        // 主键采用递增策略
        if (field.isPrimaryKey()) {
            if (StringUtils.isBlank(mockParams)) {
                mockParams = "1";
            }
            int initValue = Integer.parseInt(mockParams);
            for (int i = 0; i < rowNum; i++) {
                list.add(String.valueOf(initValue + i));
            }
            return list;
        }
        // 使用默认值
        String defaultValue = field.getDefaultValue();
        // 特殊逻辑，日期要伪造数据
        if (Constants.CURRENT_TIMESTAMP.equals(defaultValue)) {
            defaultValue = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        }
        if (StringUtils.isNotBlank(defaultValue)) {
            for (int i = 0; i < rowNum; i++) {
                list.add(defaultValue);
            }
        }
        return list;
    }
}