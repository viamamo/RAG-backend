<#-- Java 对象模板 -->
${className} ${objectName} = new ${className}();
<#-- 循环生成字段 ---------->
<#list metaFieldList as metaField>
${objectName}.${metaField.setMethod}(${metaField.value});
</#list>
