package com.kesei.rag.mocker.builder;

import cn.hutool.core.util.StrUtil;
import com.kesei.rag.exception.GenericException;
import com.kesei.rag.mocker.entity.MetaTable;
import com.kesei.rag.mocker.entity.metadata.JavaEntityMetaData;
import com.kesei.rag.mocker.entity.metadata.JavaObjectMetaData;
import com.kesei.rag.mocker.support.FieldType;
import com.kesei.rag.mocker.support.MockType;
import com.kesei.rag.mocker.support.ResponseCode;
import com.kesei.rag.mocker.support.utils.MockTool;
import com.kesei.rag.support.Constants;
import freemarker.template.Template;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author kesei
 */
@Component
@Slf4j
public class JavaCodeBuilder {
    
    @Resource
    private FreeMarkerConfigurer freeMarkerConfigurer;
    
    /**
     * 构造 Java 实体代码
     *
     * @param metaTable 表概要
     * @return 生成的 java 代码
     */
    @SneakyThrows
    public String buildJavaEntityCode(MetaTable metaTable) {
        // 传递参数
        JavaEntityMetaData javaEntityMetaData = new JavaEntityMetaData();
        String tableName = metaTable.getTableName();
        String tableComment = metaTable.getTableComment();
        String upperCamelTableName = StringUtils.capitalize(StrUtil.toCamelCase(tableName));
        // 类名为大写的表名
        javaEntityMetaData.setClassName(upperCamelTableName);
        // 类注释为表注释 > 表名
        javaEntityMetaData.setClassComment(Optional.ofNullable(tableComment).orElse(upperCamelTableName));
        // 依次填充每一列
        List<JavaEntityMetaData.MetaField> javaEntityMetaFieldList = new ArrayList<>();
        for (MetaTable.MetaField metaField : metaTable.getMetaFieldList()) {
            JavaEntityMetaData.MetaField javaEntityMetaField = new JavaEntityMetaData.MetaField();
            javaEntityMetaField.setComment(metaField.getComment());
            FieldType fieldType = Optional.ofNullable(MockTool.getFieldTypeByValue(metaField.getFieldType())).orElse(FieldType.TEXT);
            javaEntityMetaField.setJavaType(fieldType.getJavaType());
            javaEntityMetaField.setFieldName(StrUtil.toCamelCase(metaField.getFieldName()));
            javaEntityMetaFieldList.add(javaEntityMetaField);
        }
        javaEntityMetaData.setMetaFieldList(javaEntityMetaFieldList);
        
        StringWriter stringWriter = new StringWriter();
        Template temp = freeMarkerConfigurer.getConfiguration().getTemplate(Constants.JAVA_ENTITY_TEMPLATE);
        temp.process(javaEntityMetaData, stringWriter);
        return stringWriter.toString();
    }
    
    /**
     * 构造 Java 对象代码
     *
     * @param metaTable 表概要
     * @param dataList    数据列表
     * @return 生成的 java 代码
     */
    @SneakyThrows
    public String buildJavaObjectCode(MetaTable metaTable, List<Map<String, Object>> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            throw new GenericException(ResponseCode.PARAMS_ERROR, "缺少示例数据");
        }
        // 传递参数
        JavaObjectMetaData javaObjectMetaData = new JavaObjectMetaData();
        String tableName = metaTable.getTableName();
        String camelTableName = StrUtil.toCamelCase(tableName);
        // 类名为大写的表名
        javaObjectMetaData.setClassName(StringUtils.capitalize(camelTableName));
        // 变量名为表名
        javaObjectMetaData.setObjectName(camelTableName);
        // 依次填充每一列
        Map<String, Object> fillData = dataList.get(0);
        List<JavaObjectMetaData.MetaField> javaObjectMetaFieldList = new ArrayList<>();
        List<MetaTable.MetaField> metaFieldList = metaTable.getMetaFieldList();
        // 过滤掉不模拟的字段
        metaFieldList = metaFieldList.stream()
                .filter(field -> {
                    MockType mockType = Optional.ofNullable(MockTool.getMockTypeByValue(field.getMockType())).orElse(MockType.NONE);
                    return !MockType.NONE.equals(mockType);
                })
                .collect(Collectors.toList());
        for (MetaTable.MetaField metaField : metaFieldList) {
            JavaObjectMetaData.MetaField javaObjectMetaField = new JavaObjectMetaData.MetaField();
            // 驼峰字段名
            String fieldName = metaField.getFieldName();
            javaObjectMetaField.setSetMethod(StrUtil.toCamelCase("set_" + fieldName));
            javaObjectMetaField.setValue(getValueStr(metaField, fillData.get(fieldName)));
            javaObjectMetaFieldList.add(javaObjectMetaField);
        }
        javaObjectMetaData.setMetaFieldList(javaObjectMetaFieldList);
    
        StringWriter stringWriter = new StringWriter();
        Template temp = freeMarkerConfigurer.getConfiguration().getTemplate(Constants.JAVA_OBJECT_TEMPLATE);
        temp.process(javaObjectMetaData, stringWriter);
        return stringWriter.toString();
    }
    
    /**
     * 根据列的属性获取值字符串
     *
     * @param metaField
     * @param value
     * @return
     */
    public static String getValueStr(MetaTable.MetaField metaField, Object value) {
        if (metaField == null || value == null) {
            return "''";
        }
        FieldType fieldType = Optional.ofNullable(MockTool.getFieldTypeByValue(metaField.getFieldType())).orElse(FieldType.TEXT);
        return switch (fieldType) {
            case DATE, TIME, DATETIME, CHAR, VARCHAR, TINYTEXT, TEXT, MEDIUMTEXT, LONGTEXT, TINYBLOB, BLOB, MEDIUMBLOB, LONGBLOB, BINARY, VARBINARY ->
                    String.format("\"%s\"", value);
            default -> String.valueOf(value);
        };
    }
}
