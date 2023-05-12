package com.kesei.rag.mocker.generator;

import com.kesei.rag.mocker.support.MockType;
import com.kesei.rag.support.Constants;
import com.kesei.rag.support.utils.ReflectionUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author kesei
 */

@Component
@Slf4j
public class DataGeneratorFactory {
    
    /**
     * 模拟类型 => 生成器映射
     */
    private final Map<MockType, DataGenerator> MOCK_TYPE_DATA_GENERATOR_MAP = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void init() {
        ArrayList<Class<?>> classes = ReflectionUtils.getResourcesByPackage(Constants.GENERATOR_PACKAGE);
        for (Class<?> clazz : classes) {
            DataGeneratorAnnotation annotation = clazz.getAnnotation(DataGeneratorAnnotation.class);
            try {
                MOCK_TYPE_DATA_GENERATOR_MAP.put(annotation.mockType(), (DataGenerator) clazz.getDeclaredConstructor().newInstance());
                log.info("DataGeneratorFactory mounted:{}", clazz.getName());
            } catch (Exception e) {
                log.error("DataGeneratorFactory construct failed, className:{}", clazz.getName(), new RuntimeException(e));
            }
        }
    }
    
    /**
     * 获取实例
     *
     * @param mockType
     *
     * @return
     */
    public DataGenerator getGenerator(MockType mockType) {
        mockType = Optional.ofNullable(mockType).orElse(MockType.NONE);
        return MOCK_TYPE_DATA_GENERATOR_MAP.get(mockType);
    }
}