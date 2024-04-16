package com.kesei.rag.mocker.support;

/**
 * 字段类型 ANSI标准
 *
 * @author viamamo
 */
public enum FieldType {
    SMALLINT("smallint", "Integer", "number"),
    INTEGER("integer", "Integer", "number"),
    BIGINT("bigint", "Long", "number"),
    FLOAT("float", "Double", "number"),
    BOOLEAN("boolean","Boolean","boolean"),
    DOUBLE_PRECISION("double precision", "Double", "number"),
    DECIMAL("decimal", "BigDecimal", "number"),
    DATE("date", "Date", "Date"),
    TIME("time", "Time", "Date"),
    TIMESTAMP("timestamp", "Long", "number"),
    CHAR("char", "String", "string"),
    VARCHAR("varchar", "String", "string"),
    TEXT("text", "String", "string"),
    BINARY("binary", "byte[]", "string"),
    VARBINARY("varbinary", "byte[]", "string");
    
    private final String value;
    
    private final String javaType;
    
    private final String typescriptType;
    
    FieldType(String value, String javaType, String typescriptType) {
        this.value = value;
        this.javaType = javaType;
        this.typescriptType = typescriptType;
    }
    
    public String getValue() {
        return value;
    }
    
    public String getJavaType() {
        return javaType;
    }
    
    public String getTypescriptType() {
        return typescriptType;
    }
}
