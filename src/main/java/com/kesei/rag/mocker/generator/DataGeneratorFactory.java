package com.kesei.rag.mocker.generator;

import com.kesei.rag.mocker.generator.impl.*;
import com.kesei.rag.mocker.support.MockType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author kesei
 */

//TODO 组件化 通过反射扫描注解注册生成器

public class DataGeneratorFactory {
    
    /**
     * 模拟类型 => 生成器映射
     */
    private static final Map<MockType, DataGenerator> MOCK_TYPE_DATA_GENERATOR_MAP = new HashMap<>() {{
        put(MockType.NONE, new DefaultDataGenerator());
        put(MockType.FIXED, new FixedDataGenerator());
        put(MockType.RANDOM, new RandomDataGenerator());
        put(MockType.REGEX, new RegexDataGenerator());
        put(MockType.DICT, new DictDataGenerator());
        put(MockType.INCREASE, new IncreaseDataGenerator());
    }};
    
    private DataGeneratorFactory() {
    }
    
    /**
     * 获取实例
     *
     * @param mockType
     * @return
     */
    public static DataGenerator getGenerator(MockType mockType) {
        mockType = Optional.ofNullable(mockType).orElse(MockType.NONE);
        return MOCK_TYPE_DATA_GENERATOR_MAP.get(mockType);
    }
}