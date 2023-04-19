package com.kesei.rag.mocker.support.utils;

import com.kesei.rag.mocker.support.FakerType;
import com.kesei.rag.mocker.support.FieldType;
import com.kesei.rag.mocker.support.MockType;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Stream;

/**
 * @author kesei
 */
@Component
public class MockTool {
    private static final Map<String, MockType> MOCK_TYPE_MAP =new HashMap<>();
    private static final List<String> MOCK_TYPE_LIST =new ArrayList<>();
    
    private static final Map<String, FieldType> FIELD_TYPE_MAP =new HashMap<>();
    private static final List<String> FIELD_TYPE_LIST =new ArrayList<>();
    
    private static final Map<String, FakerType> FAKER_TYPE_MAP =new HashMap<>();
    private static final List<String> FAKER_TYPE_LIST =new ArrayList<>();
    
    @PostConstruct
    public void init() {
        Stream.of(MockType.values()).forEach((mockType -> {
            MOCK_TYPE_MAP.put(mockType.getValue(), mockType);
            MOCK_TYPE_LIST.add(mockType.getValue());
        }));
        Stream.of(FieldType.values()).forEach(fieldType -> {
            FIELD_TYPE_MAP.put(fieldType.getValue(), fieldType);
            FIELD_TYPE_LIST.add(fieldType.getValue());
        });
        Stream.of(FakerType.values()).forEach(fakerType -> {
            FAKER_TYPE_MAP.put(fakerType.getValue(), fakerType);
            FAKER_TYPE_LIST.add(fakerType.getValue());
        });
    }
    
    public static List<String> getMockTypeList() {
        return MOCK_TYPE_LIST;
    }
    public static MockType getMockTypeByValue(String value) {
        return MOCK_TYPE_MAP.getOrDefault(value, null);
    }
    
    public static List<String> getFieldTypeList() {
        return FIELD_TYPE_LIST;
    }
    public static FieldType getFieldTypeByValue(String value) {
        return FIELD_TYPE_MAP.getOrDefault(value, null);
    }
    
    public static List<String> getFakerTypeList() {
        return FAKER_TYPE_LIST;
    }
    public static FakerType getFakerTypeByValue(String value) {
        return FAKER_TYPE_MAP.getOrDefault(value, null);
    }
}