package com.kesei.rag.mocker.entity;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLPrimaryKey;
import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlCreateTableParser;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kesei.rag.entity.po.FieldInfo;
import com.kesei.rag.mocker.support.ResponseCode;
import com.kesei.rag.exception.GenericException;
import com.kesei.rag.mocker.support.FieldType;
import com.kesei.rag.mocker.support.MockType;
import com.kesei.rag.mocker.support.dialect.SqlDialect;
import com.kesei.rag.mocker.support.dialect.impl.MysqlDialect;
import com.kesei.rag.service.FieldInfoService;
import com.kesei.rag.support.Constants;
import com.kesei.rag.support.utils.SpringContextUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author kesei
 */
@Component
@Slf4j
public class MetaTableBuilder {
    
    private static FieldInfoService fieldInfoService;
    
    private static SqlDialect sqlDialect;
    
    @Resource
    public void setFieldInfoService(FieldInfoService fieldInfoService) {
        MetaTableBuilder.fieldInfoService = fieldInfoService;
    }
    
    @PostConstruct
    public void init() {
        sqlDialect = new MysqlDialect();
    }
    
    /**
     * 智能构建
     *
     * @param content
     *
     * @return
     */
    public static MetaTable buildFromAuto(String content) {
        if (StrUtil.isBlank(content)) {
            throw new GenericException(ResponseCode.PARAMS_ERROR);
        }
        // 切分单词
        String[] words = content.split("[,，]");
        if (ArrayUtils.isEmpty(words) || words.length > Constants.AUTO_MAX_WORD_SIZE) {
            throw new GenericException(ResponseCode.PARAMS_ERROR);
        }
        // 根据单词去词库里匹配列信息，未匹配到的使用默认值
        QueryWrapper<FieldInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("name", Arrays.asList(words)).or().in("fieldName", Arrays.asList(words));
        List<FieldInfo> fieldInfoList = fieldInfoService.list(queryWrapper);
        // 名称 => 字段信息
        Map<String, List<FieldInfo>> nameFieldInfoMap = fieldInfoList.stream().collect(Collectors.groupingBy(FieldInfo::getName));
        // 字段名称 => 字段信息
        Map<String, List<FieldInfo>> fieldNameFieldInfoMap = fieldInfoList.stream().collect(Collectors.groupingBy(FieldInfo::getFieldName));
        MetaTable metaTable = new MetaTable();
        metaTable.setTableName("my_table");
        metaTable.setTableComment("自动生成的表");
        List<MetaTable.MetaField> metaFieldList = new ArrayList<>();
        for (String word : words) {
            MetaTable.MetaField metaField;
            List<FieldInfo> infoList = Optional.ofNullable(nameFieldInfoMap.get(word))
                    .orElse(fieldNameFieldInfoMap.get(word));
            if (CollectionUtils.isNotEmpty(infoList)) {
                metaField = JSONUtil.toBean(infoList.get(0).getContent(), MetaTable.MetaField.class);
            } else {
                // 未匹配到的使用默认值
                metaField = getDefaultField(word);
            }
            metaFieldList.add(metaField);
        }
        metaTable.setMetaFieldList(metaFieldList);
        return metaTable;
    }
    
    /**
     * 根据建表 SQL 构建
     *
     * @param sql 建表 SQL
     *
     * @return 生成的 TableSchema
     */
    public static MetaTable buildFromSql(String sql) {
        if (StrUtil.isBlank(sql)) {
            throw new GenericException(ResponseCode.PARAMS_ERROR);
        }
        try {
            // 解析 SQL
            MySqlCreateTableParser parser = new MySqlCreateTableParser(sql);
            SQLCreateTableStatement sqlCreateTableStatement = parser.parseCreateTable();
            MetaTable metaTable = new MetaTable();
            metaTable.setDbName(sqlCreateTableStatement.getSchema());
            metaTable.setTableName(sqlDialect.parseTableName(sqlCreateTableStatement.getTableName()));
            String tableComment = null;
            if (sqlCreateTableStatement.getComment() != null) {
                tableComment = sqlCreateTableStatement.getComment().toString();
                if (tableComment.length() > 2) {
                    tableComment = tableComment.substring(1, tableComment.length() - 1);
                }
            }
            metaTable.setTableComment(tableComment);
            List<MetaTable.MetaField> metaFieldList = new ArrayList<>();
            // 解析列
            for (SQLTableElement sqlTableElement : sqlCreateTableStatement.getTableElementList()) {
                // 主键约束
                if (sqlTableElement instanceof SQLPrimaryKey sqlPrimaryKey) {
                    String primaryFieldName = sqlDialect.parseFieldName(sqlPrimaryKey.getColumns().get(0).toString());
                    metaFieldList.forEach(field -> {
                        if (field.getFieldName().equals(primaryFieldName)) {
                            field.setPrimaryKey(true);
                        }
                    });
                } else if (sqlTableElement instanceof SQLColumnDefinition columnDefinition) {
                    MetaTable.MetaField metaField = new MetaTable.MetaField();
                    metaField.setFieldName(sqlDialect.parseFieldName(columnDefinition.getNameAsString()));
                    metaField.setFieldType(columnDefinition.getDataType().toString());
                    String defaultValue = null;
                    if (columnDefinition.getDefaultExpr() != null) {
                        defaultValue = columnDefinition.getDefaultExpr().toString();
                    }
                    metaField.setDefaultValue(defaultValue);
                    metaField.setNotNull(columnDefinition.containsNotNullConstaint());
                    String comment = null;
                    if (columnDefinition.getComment() != null) {
                        comment = columnDefinition.getComment().toString();
                        if (comment.length() > 2) {
                            comment = comment.substring(1, comment.length() - 1);
                        }
                    }
                    metaField.setComment(comment);
                    metaField.setPrimaryKey(columnDefinition.isPrimaryKey());
                    metaField.setAutoIncrement(columnDefinition.isAutoIncrement());
                    metaField.setMockType(MockType.NONE.getValue());
                    metaFieldList.add(metaField);
                }
            }
            metaTable.setMetaFieldList(metaFieldList);
            return metaTable;
        } catch (Exception e) {
            log.error("SQL 解析错误", e);
            throw new GenericException(ResponseCode.PARAMS_ERROR, "请确认 SQL 语句正确");
        }
    }
    
    /**
     * 根据 Excel 文件构建
     *
     * @param file Excel 文件
     *
     * @return 生成的 TableSchema
     */
    public static MetaTable buildFromExcel(MultipartFile file) {
        try {
            String typeName = FileTypeUtil.getType(file.getInputStream(), true);
            if (!Objects.equals(typeName, "xlsx") && !Objects.equals(typeName, "xls")) {
                throw new GenericException(ResponseCode.PARAMS_ERROR, "文件类型错误");
            }
        } catch (IOException e) {
            throw new GenericException(ResponseCode.PARAMS_ERROR, "文件读取错误");
        }
        try {
            List<Map<Integer, String>> dataList = EasyExcel.read(file.getInputStream()).sheet().headRowNumber(0).doReadSync();
            if (CollectionUtils.isEmpty(dataList)) {
                throw new GenericException(ResponseCode.PARAMS_ERROR, "表格无数据");
            }
            // 第一行为表头
            Map<Integer, String> map = dataList.get(0);
            List<MetaTable.MetaField> metaFieldList = map.values().stream().map(name -> {
                MetaTable.MetaField metaField = new MetaTable.MetaField();
                metaField.setFieldName(name);
                metaField.setComment(name);
                metaField.setFieldType(FieldType.TEXT.getValue());
                return metaField;
            }).collect(Collectors.toList());
            // 第二行为值
            if (dataList.size() > 1) {
                Map<Integer, String> dataMap = dataList.get(1);
                for (int i = 0; i < metaFieldList.size(); i++) {
                    String value = dataMap.get(i);
                    // 根据值判断类型
                    String fieldType = getFieldTypeByValue(value);
                    metaFieldList.get(i).setFieldType(fieldType);
                }
            }
            MetaTable metaTable = new MetaTable();
            metaTable.setMetaFieldList(metaFieldList);
            return metaTable;
        } catch (GenericException genericException) {
            throw genericException;
        } catch (Exception e) {
            log.error("buildFromExcel error", e);
            throw new GenericException(ResponseCode.PARAMS_ERROR, "表格解析错误");
        }
    }
    
    /**
     * 判断字段类型
     *
     * @param value
     *
     * @return
     */
    public static String getFieldTypeByValue(String value) {
        if (StrUtil.isBlank(value)) {
            return FieldType.TEXT.getValue();
        }
        // 布尔
        if ("false".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value)) {
            return FieldType.TINYINT.getValue();
        }
        // 整数
        if (StrUtil.isNumeric(value)) {
            long number = Long.parseLong(value);
            if (number > Integer.MAX_VALUE) {
                return FieldType.BIGINT.getValue();
            }
            return FieldType.INT.getValue();
        }
        // 小数
        if (isDouble(value)) {
            return FieldType.DOUBLE.getValue();
        }
        // 日期
        if (isDate(value)) {
            return FieldType.DATETIME.getValue();
        }
        return FieldType.TEXT.getValue();
    }
    
    /**
     * 判断字符串是不是 double 型
     *
     * @param str
     *
     * @return
     */
    private static boolean isDouble(String str) {
        Pattern pattern = Pattern.compile("\\d+[.]?\\d*[dD]?");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }
    
    /**
     * 判断是否为日期
     *
     * @param str
     *
     * @return
     */
    private static boolean isDate(String str) {
        if (StrUtil.isBlank(str)) {
            return false;
        }
        try {
            DateUtils.parseDate(str, Constants.DATE_PATTERNS);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 获取默认字段
     *
     * @param word
     *
     * @return
     */
    private static MetaTable.MetaField getDefaultField(String word) {
        final MetaTable.MetaField field = new MetaTable.MetaField();
        field.setFieldName(word);
        field.setFieldType("text");
        field.setDefaultValue("");
        field.setNotNull(false);
        field.setComment(word);
        field.setPrimaryKey(false);
        field.setAutoIncrement(false);
        field.setMockType("");
        field.setMockParams("");
        return field;
    }
    
}