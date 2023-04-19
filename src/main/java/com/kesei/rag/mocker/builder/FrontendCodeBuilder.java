package com.kesei.rag.mocker.builder;

import cn.hutool.core.util.StrUtil;
import com.kesei.rag.mocker.entity.MetaTable;
import com.kesei.rag.mocker.entity.metadata.TypescriptEntityMetaData;
import com.kesei.rag.mocker.support.FieldType;
import com.kesei.rag.mocker.support.utils.MockTool;
import com.kesei.rag.support.Constants;
import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author kesei
 */
@Component
@Slf4j
public class FrontendCodeBuilder {
    
    private static Configuration configuration;
    
    @Resource
    public void setConfiguration(Configuration configuration) {
        FrontendCodeBuilder.configuration = configuration;
    }
    
    /**
     * 构造 Typescript 类型代码
     *
     * @param metaTable 表概要
     * @return 生成的代码
     */
    @SneakyThrows
    public String buildTypeScriptTypeCode(MetaTable metaTable) {
        // 传递参数
        TypescriptEntityMetaData typescriptEntityMetaData = new TypescriptEntityMetaData();
        String tableName = metaTable.getTableName();
        
        String tableComment = metaTable.getTableComment();
        String upperCamelTableName = StringUtils.capitalize(StrUtil.toCamelCase(tableName));
        // 类名为大写的表名
        typescriptEntityMetaData.setClassName(upperCamelTableName);
        // 类注释为表注释 > 表名
        typescriptEntityMetaData.setClassComment(Optional.ofNullable(tableComment).orElse(upperCamelTableName));
        // 依次填充每一列
        List<TypescriptEntityMetaData.MetaField> tsMetaFieldList = new ArrayList<>();
        for (MetaTable.MetaField metaField : metaTable.getMetaFieldList()) {
            TypescriptEntityMetaData.MetaField tsMetaField = new TypescriptEntityMetaData.MetaField();
            tsMetaField.setComment(metaField.getComment());
            FieldType fieldType = Optional.ofNullable(MockTool.getFieldTypeByValue(metaField.getFieldType())).orElse(FieldType.TEXT);
            tsMetaField.setTypescriptType(fieldType.getTypescriptType());
            tsMetaField.setFieldName(StrUtil.toCamelCase(metaField.getFieldName()));
            tsMetaFieldList.add(tsMetaField);
        }
        typescriptEntityMetaData.setMetaFieldList(tsMetaFieldList);
    
        StringWriter stringWriter=new StringWriter();
        Template template=configuration.getTemplate(Constants.FRONT_CODE_TEMPLATE);
        template.process(typescriptEntityMetaData, stringWriter);
        return stringWriter.toString();
    }
}
