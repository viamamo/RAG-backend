package com.kesei.rag.mocker;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.kesei.rag.entity.vo.GenerationVO;
import com.kesei.rag.mocker.builder.*;
import com.kesei.rag.mocker.entity.MetaTable;
import com.kesei.rag.mocker.exception.MetaTableException;
import com.kesei.rag.mocker.support.dialect.SqlDialect;
import com.kesei.rag.mocker.support.dialect.SqlDialectFactory;
import com.kesei.rag.mocker.support.utils.MockTool;
import com.kesei.rag.support.Constants;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author kesei
 */
@Component
@Slf4j
public class GeneratorFacade {
    
    private static DataBuilder dataBuilder;
    private static FrontendCodeBuilder frontendCodeBuilder;
    private static JavaCodeBuilder javaCodeBuilder;
    private static JsonBuilder jsonBuilder;
    
    @Resource
    public void setDataBuilder(DataBuilder dataBuilder) {
        GeneratorFacade.dataBuilder = dataBuilder;
    }
    
    @Resource
    public void setFrontendCodeBuilder(FrontendCodeBuilder frontendCodeBuilder) {
        GeneratorFacade.frontendCodeBuilder = frontendCodeBuilder;
    }
    
    @Resource
    public void setJavaCodeBuilder(JavaCodeBuilder javaCodeBuilder) {
        GeneratorFacade.javaCodeBuilder = javaCodeBuilder;
    }
    
    @Resource
    public void setJsonBuilder(JsonBuilder jsonBuilder) {
        GeneratorFacade.jsonBuilder = jsonBuilder;
    }
    
    
    /**
     * 生成所有内容
     *
     * @param metaTable
     *
     * @return
     */
    public static GenerationVO generateAll(MetaTable metaTable) {
        long start=System.currentTimeMillis();
        
        // 校验
        validMetaTable(metaTable);
        SqlDialect sqlDialect = SqlDialectFactory.getDialect(MockTool.getDatabaseTypeByName(metaTable.getDbType()));
        SqlBuilder sqlBuilder = new SqlBuilder(sqlDialect);
        // 构造建表 SQL
        String createSql = sqlBuilder.buildCreateTableSql(metaTable);
        int mockNum = metaTable.getMockNum();
        long start2=System.currentTimeMillis();
        // 生成模拟数据
        List<Map<String, Object>> dataList = dataBuilder.generateData(metaTable, mockNum);
    
        log.info("数据生成时间：{}", System.currentTimeMillis()-start2);
        // 生成插入 SQL
        String insertSql = sqlBuilder.buildInsertSql(metaTable, dataList);
        // 生成数据 json
        String dataJson = jsonBuilder.buildJson(dataList);
        // 生成 java 实体代码
        String javaEntityCode = javaCodeBuilder.buildJavaEntityCode(metaTable);
        // 生成 java 对象代码
        String javaObjectCode = javaCodeBuilder.buildJavaObjectCode(metaTable, dataList);
        // 生成 typescript 类型代码
        String typescriptTypeCode = frontendCodeBuilder.buildTypeScriptTypeCode(metaTable);
        // 封装返回
        GenerationVO generationVO = new GenerationVO();
        generationVO.setMetaTable(metaTable);
        generationVO.setCreateSql(createSql);
        generationVO.setDataList(dataList);
        generationVO.setInsertSql(insertSql);
        generationVO.setDataJson(dataJson);
        generationVO.setJavaEntityCode(javaEntityCode);
        generationVO.setJavaObjectCode(javaObjectCode);
        generationVO.setTypescriptTypeCode(typescriptTypeCode);
        
        
        log.info("总体生成时间：{}", System.currentTimeMillis()-start);
        return generationVO;
    }
    
    /**
     * 校验MetaTable
     *
     * @param metaTable 表定义
     */
    public static void validMetaTable(MetaTable metaTable) {
        if (metaTable == null) {
            throw new MetaTableException("数据为空");
        }
        String tableName = metaTable.getTableName();
        if (StrUtil.isBlank(tableName)) {
            throw new MetaTableException("表名不能为空");
        }
        // 默认生成 20 条
        if (metaTable.getMockNum() == null) {
            metaTable.setMockNum(Constants.SIMPLE_DEFAULT_MOCK_NUM);
        }
        Integer mockNum = metaTable.getMockNum();
        if (mockNum < Constants.SIMPLE_MIN_MOCK_NUM || mockNum > Constants.SIMPLE_MAX_MOCK_NUM) {
            throw new MetaTableException("生成条数设置错误");
        }
        List<MetaTable.MetaField> metaFieldList = metaTable.getMetaFieldList();
        if (CollectionUtils.isEmpty(metaFieldList)) {
            throw new MetaTableException("字段列表不能为空");
        }
        for (MetaTable.MetaField metaField : metaFieldList) {
            validMetaField(metaField);
        }
    }
    
    /**
     * 校验MetaTable
     *
     * @param metaTable 表定义
     */
    public static void validMetaTableJob(MetaTable metaTable) {
        if (metaTable == null) {
            throw new MetaTableException("数据为空");
        }
        String tableName = metaTable.getTableName();
        if (StrUtil.isBlank(tableName)) {
            throw new MetaTableException("表名不能为空");
        }
        // 默认生成 20 条
        if (metaTable.getMockNum() == null) {
            metaTable.setMockNum(Constants.SIMPLE_DEFAULT_MOCK_NUM);
        }
        Integer mockNum = metaTable.getMockNum();
        List<MetaTable.MetaField> metaFieldList = metaTable.getMetaFieldList();
        if (CollectionUtils.isEmpty(metaFieldList)) {
            throw new MetaTableException("字段列表不能为空");
        }
        for (MetaTable.MetaField metaField : metaFieldList) {
            validMetaField(metaField);
        }
    }
    
    /**
     * 校验MetaField
     *
     * @param metaField 字段定义
     */
    public static void validMetaField(MetaTable.MetaField metaField) {
        String fieldName = metaField.getFieldName();
        String fieldType = metaField.getFieldType();
        if (StrUtil.isBlank(fieldName)) {
            throw new MetaTableException("字段名不能为空");
        }
        if (StrUtil.isBlank(fieldType)) {
            throw new MetaTableException("字段类型不能为空");
        }
    }
    
}