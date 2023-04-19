<#-- Typescript 类型生成模板 -->
/**
 * ${classComment}
 */
interface ${className} {
<#-- 循环生成字段 ---------->
<#list metaFieldList as metaField>
  // ${metaField.comment}
  ${metaField.fieldName}: ${metaField.typescriptType};
</#list>
}
