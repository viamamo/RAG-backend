package com.kesei.rag.service.impl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kesei.rag.entity.po.DbInfo;
import com.kesei.rag.entity.po.JobInfo;
import com.kesei.rag.exception.GenericException;
import com.kesei.rag.mapper.JobInfoMapper;
import com.kesei.rag.mocker.builder.DataBuilder;
import com.kesei.rag.mocker.builder.SqlBuilder;
import com.kesei.rag.mocker.entity.MetaTable;
import com.kesei.rag.mocker.support.ResponseCode;
import com.kesei.rag.mocker.support.dialect.SqlDialect;
import com.kesei.rag.service.JobInfoService;
import com.kesei.rag.support.Constants;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

/**
 * @author kesei
 */
@Service
@Slf4j
public class JobInfoServiceImpl extends ServiceImpl<JobInfoMapper, JobInfo> implements JobInfoService {
    
    @Resource
    private Environment config;
    
    private DataBuilder dataBuilder;
    
    private DataSource systemDataSource;
    
    private final Map<Long, DataSource> dataSourceMap = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void init() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("url", config.getProperty("spring.datasource.url"));
        properties.setProperty("username", config.getProperty("spring.datasource.username"));
        properties.setProperty("password", config.getProperty("spring.datasource.password"));
        properties.setProperty("maxActive", config.getProperty("spring.datasource.max-active", String.valueOf(Constants.MAX_ACTIVE_CONNECTION)));
        systemDataSource = DruidDataSourceFactory.createDataSource(properties);
        dataBuilder = new DataBuilder();
    }
    
    @Override
    public void handleAdd(JobInfo jobInfo, String metaTableString, DbInfo dbInfo) {
        MetaTable metaTable = JSONUtil.toBean(metaTableString, MetaTable.class);
        jobInfo.setDbId(dbInfo.getId());
        jobInfo.setFinishedNum(0);
        jobInfo.setMockNum(metaTable.getMockNum());
        jobInfo.setStatus(0);
        jobInfo.setContent(metaTableString);
        jobInfo.setTableName(metaTable.getTableName());
        jobInfo.setDbName(dbInfo.getDbName());
        jobInfo.setDbType(dbInfo.getDbType());
        jobInfo.setHost(dbInfo.getHost());
        jobInfo.setPort(dbInfo.getPort());
        jobInfo.setProperty(dbInfo.getProperty());
    }
    
    private DataSource getDataSource(DbInfo dbInfo) {
        Long id = dbInfo.getId();
        if (dataSourceMap.containsKey(id)) {
            return dataSourceMap.get(id);
        } else {
            try {
                Properties properties = new Properties();
                properties.setProperty("url", dbInfo.getUrl());
                properties.setProperty("username", dbInfo.getUsername());
                properties.setProperty("password", dbInfo.getPassword());
                properties.setProperty("maxActive", String.valueOf(Constants.MAX_ACTIVE_CONNECTION));
                if (StrUtil.isNotBlank(dbInfo.getDriver())) {
                    properties.setProperty("driver", dbInfo.getDriver());
                }
                return DruidDataSourceFactory.createDataSource(properties);
            } catch (Exception e) {
                throw new GenericException(ResponseCode.SQL_OPERATION_ERROR);
            }
        }
    }
    
    public Connection getSystemConnection() throws SQLException {
        return systemDataSource.getConnection();
    }
    
    public Connection getConnection(DbInfo dbInfo) {
        DataSource dataSource = getDataSource(dbInfo);
        try {
            return dataSource.getConnection();
        } catch (Exception e) {
            throw new GenericException(ResponseCode.SQL_OPERATION_ERROR);
        }
    }
    
    public Boolean executeSimpleSql(Connection connection, String sqlString) {
        try {
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();
            List<String> sqlList = CharSequenceUtil.splitTrim(sqlString, ";");
            for (String sql : sqlList) {
                statement.addBatch(sql);
            }
            statement.executeBatch();
            connection.commit();
            connection.close();
            return true;
        } catch (Exception e) {
            try {
                connection.rollback();
                throw new GenericException(ResponseCode.SQL_OPERATION_ERROR, e.getMessage());
            } catch (SQLException e2) {
                throw new GenericException(ResponseCode.SQL_OPERATION_ERROR, e2.getMessage());
            }
        }
    }
    
    @Async
    public void executeJob(JobInfo jobInfo, Connection systemConnection, Connection connection) {
        log.info("start execute");
        Integer finishedNum = jobInfo.getFinishedNum();
        Integer mockNum = jobInfo.getMockNum();
        SqlBuilder sqlBuilder = new SqlBuilder();
        SqlDialect sqlDialect =sqlBuilder.sqlDialect;
        MetaTable metaTable = JSONUtil.toBean(jobInfo.getContent(), MetaTable.class);
        
        try {
            connection.createStatement().execute(sqlBuilder.buildCreateTableSql(metaTable));
            ResultSet resultSet=connection.createStatement().executeQuery("SELECT `COLUMN_NAME` FROM `INFORMATION_SCHEMA`.`COLUMNS` WHERE `TABLE_SCHEMA`='" + jobInfo.getDbName()
                    + "' AND `TABLE_NAME`='" + jobInfo.getTableName()
                    + "' AND `COLUMN_NAME`='__rag_data_marker'");
            if(!resultSet.next()) {
                connection.createStatement().execute("ALTER TABLE`" + jobInfo.getTableName() + "` ADD COLUMN `__rag_data_marker` bigint DEFAULT " + jobInfo.getId());
            }
        } catch (SQLException e) {
            throw new GenericException(ResponseCode.SQL_OPERATION_ERROR);
        }
        
        try {
            CompletableFuture<Boolean> completableFuture=CompletableFuture.supplyAsync(()->true);
            Statement statement = connection.createStatement();
            systemConnection.createStatement().execute("UPDATE job_info SET status=1 WHERE id="+jobInfo.getId());
            String tableName = sqlBuilder.sqlDialect.wrapTableName(metaTable.getTableName());
            if (StrUtil.isNotBlank(metaTable.getDbName())) {
                tableName = String.format("%s.%s", metaTable.getDbName(), tableName);
            }
            PreparedStatement insertJob = connection.prepareStatement("INSERT INTO "+tableName+"(?) VALUES ?");
            PreparedStatement updateJob = systemConnection.prepareStatement("UPDATE job_info SET finishedNum=? WHERE id="+jobInfo.getId());
            log.info("start mock");
            int blockNum=0;
            int blockSize=mockNum/20;
            while (finishedNum < mockNum) {
                blockSize = Math.min(mockNum - finishedNum, blockSize);
                List<Map<String,Object>> mapList=dataBuilder.generateDataWithBlock(metaTable,blockNum, blockSize);
                List<String> sqlList = sqlBuilder.buildInsertSqlList(metaTable, mapList);
                for (String sql : sqlList) {
                    statement.addBatch(sql);
                }
                if(completableFuture.get()) {
                    int finalBlockNum = blockNum;
                    int finalBlockSize = blockSize;
                    completableFuture = CompletableFuture.supplyAsync(() -> {
                        long start=System.currentTimeMillis();
                        try {
                            statement.executeBatch();
                            statement.clearBatch();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        log.info("blockNumber:{},blockSize:{},time:{}s", finalBlockNum, finalBlockSize,(double)(System.currentTimeMillis()-start)/1000);
                        return true;
                    });
                }
                updateJob.setInt(1, finishedNum);
                updateJob.execute();
                blockNum++;
                finishedNum += blockSize;
                log.info("finishedNum:{}", finishedNum);
            }
        } catch (Exception e) {
            try {
                PreparedStatement exception = systemConnection.prepareStatement("UPDATE job_info SET status=4,exception=? WHERE id=?");
                exception.setString(1, e.getMessage());
                exception.setLong(2, jobInfo.getId());
                exception.execute();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
        try {
            systemConnection.close();
            connection.close();
        }catch (SQLException e){
            log.error("jobId:{},connection close failed\nexception message:{}",jobInfo.getId(), e.getMessage());
        }
        log.info("finished");
    }
}
