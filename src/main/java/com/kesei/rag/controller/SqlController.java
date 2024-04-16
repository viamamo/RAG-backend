package com.kesei.rag.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.kesei.rag.entity.dto.GenericPostRequest;
import com.kesei.rag.entity.dto.GenericResponse;
import com.kesei.rag.entity.dto.sql.GenerateFromSqlRequest;
import com.kesei.rag.entity.vo.GenerationVO;
import com.kesei.rag.exception.GenericException;
import com.kesei.rag.mocker.entity.MetaTable;
import com.kesei.rag.mocker.support.ResponseCode;
import com.kesei.rag.mocker.support.dialect.SqlDialect;
import com.kesei.rag.mocker.support.dialect.SqlDialectFactory;
import com.kesei.rag.mocker.support.utils.MockTool;
import com.kesei.rag.service.SqlService;
import com.kesei.rag.support.BasicInfo;
import com.kesei.rag.support.utils.ResponseUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author viamamo
 */
@RestController
@RequestMapping("/sql")
@Slf4j
public class SqlController {
    
    @Resource
    private SqlService sqlService;
    
    @Resource
    private BasicInfo basicInfo;
    
    private final ThreadLocal<GenerationVO> generationVOThreadLocal=new ThreadLocal<>();
    
    /**
     * 根据 MetaTable 生成
     *
     * @param metaTable MetaTable
     * @return 生成结果
     */
    @PostMapping("/generate/schema")
    public GenericResponse<GenerationVO> generateBySchema(@RequestBody MetaTable metaTable) {
        GenerationVO generationVO=sqlService.generateBySchema(metaTable);
        generationVOThreadLocal.set(generationVO);
        return ResponseUtils.success(generationVO);
    }
    
    /**
     * 根据语义自动获取 MetaTable
     *
     * @param autoPostRequest post封装
     * @return 解析完成的MetaTable
     */
    @PostMapping("/get/schema/auto")
    public GenericResponse<MetaTable> getSchemaByAuto(@RequestBody GenericPostRequest autoPostRequest) {
        if (autoPostRequest == null) {
            throw new GenericException(ResponseCode.PARAMS_ERROR);
        }
        return ResponseUtils.success(sqlService.getSchemaByAuto(autoPostRequest.getContent()));
    }
    
    /**
     * 根据 SQL 获取 MetaTable
     *
     * @param generateFromSqlRequest post封装
     * @return 解析完成的MetaTable
     */
    @PostMapping("/get/schema/sql")
    public GenericResponse<MetaTable> getSchemaBySql(@RequestBody GenerateFromSqlRequest generateFromSqlRequest) {
        if (generateFromSqlRequest == null) {
            throw new GenericException(ResponseCode.PARAMS_ERROR);
        }
        SqlDialect sqlDialect= SqlDialectFactory.getDialect(MockTool.getDatabaseTypeByStr(generateFromSqlRequest.getDbType()));
        return ResponseUtils.success(sqlService.getSchemaBySql(generateFromSqlRequest.getContent(), sqlDialect));
    }
    
    /**
     * 根据Excel 获取 MetaTable
     *
     * @param file excel文件
     * @return 解析完成的MetaTable
     */
    
    @PostMapping("/get/schema/excel")
    public GenericResponse<MetaTable> getSchemaByExcel(MultipartFile file) {
        return ResponseUtils.success(sqlService.getSchemaByExcel(file));
    }
    
    /**
     * 下载模拟数据 Excel
     *
     * @param generationVO 生成结果
     */
    @PostMapping("/download/data/excel")
    public void downloadDataExcel(@RequestBody GenerationVO generationVO, HttpServletResponse response) {
        MetaTable metaTable = generationVO.getMetaTable();
        String tableName = metaTable.getTableName();
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode(tableName + "表数据", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            // 设置表头
            List<List<String>> headList = new ArrayList<>();
            for (MetaTable.MetaField metaField : metaTable.getMetaFieldList()) {
                List<String> head = Collections.singletonList(metaField.getFieldName());
                headList.add(head);
            }
            List<String> fieldNameList = metaTable.getMetaFieldList().stream()
                    .map(MetaTable.MetaField::getFieldName).toList();
            // 设置数据
            List<List<Object>> dataList = new ArrayList<>();
            for (Map<String, Object> data : generationVO.getDataList()) {
                List<Object> dataRow = fieldNameList.stream().map(data::get).collect(Collectors.toList());
                dataList.add(dataRow);
            }
            // 这里需要设置不关闭流
            EasyExcel.write(response.getOutputStream())
                    .autoCloseStream(Boolean.FALSE)
                    .head(headList)
                    .sheet(tableName + "表")
                    .doWrite(dataList);
        } catch (Exception e) {
            // 重置 response
            response.reset();
            throw new GenericException(ResponseCode.SYSTEM_ERROR, "下载失败");
        }
    }
    
    /**
     * 全局信息初始化
     * @return 全局信息
     */
    @GetMapping("/get_basic_info")
    public GenericResponse<JSONObject> getBasicInfo(){
        return ResponseUtils.success(JSONUtil.parseObj(basicInfo));
    }
}
