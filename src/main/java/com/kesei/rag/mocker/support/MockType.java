package com.kesei.rag.mocker.support;

/**
 * @author kesei
 */
public enum MockType {
    /**
     * 模拟类型
     */
    NONE("不模拟"),
    INCREASE("递增"),
    FIXED("固定"),
    RANDOM("随机"),
    REGEX("正则"),
    DICT("词库");
    
    private final String value;
    
    MockType(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
}
