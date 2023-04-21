package com.kesei.rag.controller;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.kesei.rag.entity.dto.GenericResponse;
import com.kesei.rag.entity.dto.job.SimpleExecutionRequest;
import com.kesei.rag.entity.po.DbInfo;
import com.kesei.rag.exception.GenericException;
import com.kesei.rag.mocker.support.ResponseCode;
import com.kesei.rag.service.DbInfoService;
import com.kesei.rag.support.Constants;
import com.kesei.rag.support.utils.ResponseUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author kesei
 */
@RestController
@RequestMapping("job")
@Slf4j
public class JobController {
    
    @Resource
    DbInfoService dbInfoService;
    
    public Map<Long, DataSource> dataSourceMap=new ConcurrentHashMap<>();
    
    public DataSource getDataSource(DbInfo dbInfo) {
        Long id=dbInfo.getId();
        if(dataSourceMap.containsKey(id)){
            return dataSourceMap.get(id);
        }
        else{
            try {
                Properties properties=new Properties();
                properties.setProperty("url",dbInfo.getUrl());
                properties.setProperty("username", dbInfo.getUsername());
                properties.setProperty("password", dbInfo.getPassword());
                properties.setProperty("maxActive", String.valueOf(Constants.MAX_ACTIVE_CONNECTION));
                if(StrUtil.isNotBlank(dbInfo.getDriver())){
                    properties.setProperty("driver", dbInfo.getDriver());
                }
                return DruidDataSourceFactory.createDataSource(properties);
            }catch (Exception e){
                throw new GenericException(ResponseCode.SQL_OPERATION_ERROR);
            }
        }
    }
    
    public Connection getConnection(DbInfo dbInfo){
        DataSource dataSource = getDataSource(dbInfo);
        try{
            return dataSource.getConnection();
        }catch (Exception e){
            throw new GenericException(ResponseCode.SQL_OPERATION_ERROR);
        }
    }
    
    public Boolean executeSql(Connection connection, String sqlString){
        try {
            connection.setAutoCommit(false);
            Statement statement=connection.createStatement();
            List<String> sqlList= CharSequenceUtil.splitTrim(sqlString, ";");
            for(String sql:sqlList){
                statement.addBatch(sql);
            }
            statement.executeBatch();
            connection.commit();
            connection.close();
            return true;
        }catch (Exception e){
            try {
                connection.rollback();
                throw new GenericException(ResponseCode.SQL_OPERATION_ERROR,e.getMessage());
            }catch (SQLException e2){
                throw new GenericException(ResponseCode.SQL_OPERATION_ERROR,e2.getMessage());
            }
        }
    }
    
    @RequestMapping("/execute/simple")
    public GenericResponse<Boolean> executeSqlSimple(@RequestBody SimpleExecutionRequest simpleExecutionRequest){
        DbInfo dbInfo=dbInfoService.getById(simpleExecutionRequest.getDbInfoId());
        Connection connection=getConnection(dbInfo);
        return ResponseUtils.success(StrUtil.isNotBlank(simpleExecutionRequest.getSql())?executeSql(connection, simpleExecutionRequest.getSql()):true);
    }
}
