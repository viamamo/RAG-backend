<#-- Java 实体模板 -->
import lombok.Data;

/**
 * ${classComment}
 */
@Data
public class ${className} implements Serializable {

    <#-- 序列化 -->
    private static final long serialVersionUID = 1L;

<#-- 循环生成字段 ---------->
<#list metaFieldList as metaField>
    <#if metaField.comment!?length gt 0>
    /**
     * ${metaField.comment}
     */
    </#if>
    private ${metaField.javaType} ${metaField.fieldName};

</#list>
}
