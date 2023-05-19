package com.kesei.rag.mocker.support;

/**
 * Faker模拟数据类型
 *
 * @author kesei
 */
public enum FakerType {
    STRING("字符串"),
    NAME("人名"),
    CITY("城市"),
    URL("网址"),
    EMAIL("邮箱"),
    IP("IP"),
    INTEGER("整数"),
    DECIMAL("小数"),
    UNIVERSITY("大学"),
    DATE("日期"),
    TIMESTAMP("时间戳"),
    PHONE("手机号");
    
    private final String value;
    
    FakerType(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
}
