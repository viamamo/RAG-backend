package com.kesei.rag.mocker.support;

/**
 * 模拟类型
 *
 * @author kesei
 */
public enum MockType {
    NONE("不模拟", ""),
    INCREASE("递增", "起始值"),
    FIXED("固定", "固定值"),
    RANDOM("随机", "随机规则"),
    REGEX("正则", "正则表达式"),
    DICT("词库", "词库");
    
    private final String value;
    
    private final String mockParamName;
    
    MockType(String value, String mockParamName) {
        this.value = value;
        this.mockParamName = mockParamName;
    }
    
    public String getValue() {
        return value;
    }
    
    public String getMockParamName() {
        return mockParamName;
    }
}
