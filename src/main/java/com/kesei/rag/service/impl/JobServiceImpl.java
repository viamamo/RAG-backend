package com.kesei.rag.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kesei.rag.entity.po.DbInfo;
import com.kesei.rag.entity.po.JobInfo;
import com.kesei.rag.exception.GenericException;
import com.kesei.rag.mapper.JobInfoMapper;
import com.kesei.rag.mocker.GeneratorFacade;
import com.kesei.rag.mocker.builder.DataBuilder;
import com.kesei.rag.mocker.builder.SqlBuilder;
import com.kesei.rag.mocker.entity.MetaTable;
import com.kesei.rag.mocker.support.DatabaseType;
import com.kesei.rag.mocker.support.MockType;
import com.kesei.rag.mocker.support.ResponseCode;
import com.kesei.rag.mocker.support.dialect.SqlDialect;
import com.kesei.rag.mocker.support.dialect.SqlDialectFactory;
import com.kesei.rag.mocker.support.utils.MockTool;
import com.kesei.rag.service.JobService;
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
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author viamamo
 */
@Service
@Slf4j
public class JobServiceImpl extends ServiceImpl<JobInfoMapper, JobInfo> implements JobService {
    
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
        GeneratorFacade.validMetaTableJob(metaTable);
        for(MetaTable.MetaField metaField:metaTable.getMetaFieldList()){
            GeneratorFacade.validMetaField(metaField);
        }
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
                DruidDataSource dataSource= (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);
                dataSource.setBreakAfterAcquireFailure(true);
                dataSource.setFailFast(true);
                dataSourceMap.put(dbInfo.getId(), dataSource);
                return dataSource;
            } catch (Exception e) {
                throw new GenericException(ResponseCode.SQL_OPERATION_ERROR);
            }
        }
    }
    
    @Override
    public Connection getSystemConnection() throws SQLException {
        return systemDataSource.getConnection();
    }
    
    @Override
    public Connection getConnection(DbInfo dbInfo) {
        DataSource dataSource = getDataSource(dbInfo);
        try {
            return dataSource.getConnection();
        } catch (Exception e) {
            throw new GenericException(ResponseCode.SQL_OPERATION_ERROR,"获取连接失败:"+e.getMessage());
        }
    }
    
    @Override
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
    
    @Override
    @Async
    public void executeJob(JobInfo jobInfo, Connection systemConnection, Connection connection) {
        long start1=System.currentTimeMillis();
        long dataGenTime=0;
        log.info("start execute");
        Integer finishedNumSum=jobInfo.getFinishedNum();
        int finishedNum = 0;
        Integer mockNum = jobInfo.getMockNum();
        DatabaseType databaseType=MockTool.getDatabaseTypeByStr(jobInfo.getDbType());
        SqlDialect sqlDialect = SqlDialectFactory.getDialect(databaseType);
        SqlBuilder sqlBuilder = new SqlBuilder(sqlDialect);
        MetaTable metaTable = JSONUtil.toBean(jobInfo.getContent(), MetaTable.class);
        String tableName = sqlBuilder.sqlDialect.wrapTableName(metaTable.getTableName());
        if (StrUtil.isNotBlank(metaTable.getDbName())) {
            tableName = String.format("%s.%s", metaTable.getDbName(), tableName);
        }
        try {
            connection.setAutoCommit(false);
            String createTableSql=sqlBuilder.buildCreateTableSql(metaTable);
            log.info("{}", createTableSql);
            connection.createStatement().execute(createTableSql);
            connection.commit();
            PreparedStatement preparedStatement = connection.prepareStatement(sqlDialect.getColumnIsExistSql(jobInfo, Constants.DEFAULT_JOB_MARKER_COLUMN));
            ResultSet resultSet= preparedStatement.executeQuery();
            if (!resultSet.next()) {
                connection.createStatement().execute("ALTER TABLE " + sqlDialect.wrapTableName(jobInfo.getTableName()) + " ADD COLUMN "+sqlDialect.wrapFieldName(Constants.DEFAULT_JOB_MARKER_COLUMN)+" bigint");
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
                connection.close();
                systemConnection.close();
            } catch (SQLException ex) {
                throw new GenericException(ResponseCode.SQL_OPERATION_ERROR,ex.getMessage());
            }
            throw new GenericException(ResponseCode.SQL_OPERATION_ERROR, e.getMessage());
        }
        
        try {
            metaTable.setMetaFieldList(metaTable.getMetaFieldList().stream()
                    .filter(field -> {
                        MockType mockType = Optional.ofNullable(MockTool.getMockTypeByValue(field.getMockType()))
                                .orElse(MockType.NONE);
                        return !MockType.NONE.equals(mockType);
                    })
                    .collect(Collectors.toList()));
            CompletableFuture<Boolean> completableFuture = CompletableFuture.supplyAsync(() -> true);
            systemConnection.createStatement().execute("UPDATE job_info SET status=1 WHERE id=" + jobInfo.getId());
    
            PreparedStatement insertJob=connection.prepareStatement("INSERT INTO " + tableName + "() VALUES ()");
            PreparedStatement insertEmptyJob = connection.prepareStatement("INSERT INTO " + tableName + "("+sqlDialect.wrapFieldName(Constants.DEFAULT_JOB_MARKER_COLUMN)+") VALUES ("+jobInfo.getId()+")");
            PreparedStatement updateJob = systemConnection.prepareStatement("UPDATE job_info SET finishedNum=? WHERE id=" + jobInfo.getId());
            
            int blockNum = 0;
            int blockSize = Math.min(Math.max(100,mockNum/1000),Constants.DEFAULT_BLOCK_SIZE);
            boolean emptySql = CollectionUtil.isEmpty(dataBuilder.generateData(metaTable, 1));
            if (emptySql){
                for (int i = 0; i < blockSize; i++) {
                    insertEmptyJob.addBatch();
                }
            }
    
            log.info("start mock");
            while (finishedNum < mockNum) {
                if (emptySql) {
                    insertEmptyJob.executeBatch();
                    connection.commit();
                }
                else {
                    blockSize = Math.min(mockNum - finishedNum, blockSize);
                    
                    long dataGenFrom=System.currentTimeMillis();
                    List<Map<String, Object>> mapList = dataBuilder.generateDataByBlock(metaTable, blockNum, blockSize);
                    dataGenTime+=System.currentTimeMillis()-dataGenFrom;
                    int finalBlockNum = blockNum;
                    int finalBlockSize = blockSize;
                    if (completableFuture.get()) {
                        insertJob.clearBatch();
                        StringBuilder fieldListBuilder = new StringBuilder();
                        StringBuilder valueListBuilder = new StringBuilder();
                        for (Map<String, Object> map : mapList) {
                            if (StrUtil.isBlank(fieldListBuilder)) {
                                for (Map.Entry<String, Object> entry : map.entrySet()) {
                                    fieldListBuilder.append(",").append(sqlDialect.wrapFieldName(entry.getKey()));
                                    valueListBuilder.append(",").append("?");
                                }
                                insertJob = connection.prepareStatement("INSERT INTO " + tableName + "("+sqlDialect.wrapFieldName(Constants.DEFAULT_JOB_MARKER_COLUMN)+fieldListBuilder+") VALUES ("+jobInfo.getId()+valueListBuilder+")");
                            }
                            int i=1;
                            for (Map.Entry<String, Object> entry : map.entrySet()) {
                                insertJob.setString(i, entry.getValue().toString());
                                i++;
                            }
                            insertJob.addBatch();
                        }
    
                        PreparedStatement finalInsertJob = insertJob;
                        completableFuture = CompletableFuture.supplyAsync(() -> {
                            long start = System.currentTimeMillis();
                            try {
                                finalInsertJob.executeBatch();
                                connection.commit();
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                            log.info("blockNumber:{},blockSize:{},time:{}s", finalBlockNum, finalBlockSize, (double) (System.currentTimeMillis() - start) / 1000);
                            return true;
                        });
                    }
                }
                blockNum++;
                finishedNum += blockSize;
                finishedNumSum+=blockSize;
                updateJob.setInt(1, finishedNumSum);
                updateJob.execute();
                log.info("finishedNum:{},finishedNumSum:{}", finishedNum,finishedNumSum);
            }
        } catch (Exception e) {
            try {
                log.error("{}",e.getMessage());
                PreparedStatement exception = systemConnection.prepareStatement("UPDATE job_info SET status=4,exception=? WHERE id=?");
                exception.setString(1, e.getMessage());
                exception.setLong(2, jobInfo.getId());
                exception.execute();
                connection.close();
                systemConnection.close();
            } catch (SQLException ex) {
                throw new GenericException(ResponseCode.SQL_OPERATION_ERROR, e.getMessage());
            }
        }
        try {
            PreparedStatement updateJob = systemConnection.prepareStatement("UPDATE job_info SET status=2 WHERE id=" + jobInfo.getId());
            updateJob.execute();
            systemConnection.close();
            connection.close();
        } catch (SQLException e) {
            log.error("jobId:{},connection close failed\nexception message:{}", jobInfo.getId(), e.getMessage());
        }
        log.info("jobId:{}finished,mockNum:{},dataGenTime:{},totalTime:{}",jobInfo.getId(),mockNum,dataGenTime,System.currentTimeMillis()-start1);
    }
    
    @Override
    public boolean rollbackJob(JobInfo jobInfo, Connection systemConnection, Connection connection) {
        log.info("start rollback,jobId:{}",jobInfo.getId());
        try {
            PreparedStatement updateJob = systemConnection.prepareStatement("UPDATE job_info SET status=1 WHERE id=" + jobInfo.getId());
            updateJob.execute();
        } catch (SQLException e) {
            try {
                connection.close();
                systemConnection.close();
            } catch (SQLException ex) {
                throw new GenericException(ResponseCode.SQL_OPERATION_ERROR, ex.getMessage());
            }
            throw new GenericException(ResponseCode.SQL_OPERATION_ERROR, e.getMessage());
        }
        DatabaseType databaseType=MockTool.getDatabaseTypeByStr(jobInfo.getDbType());
        SqlDialect sqlDialect = SqlDialectFactory.getDialect(databaseType);
        MetaTable metaTable = JSONUtil.toBean(jobInfo.getContent(), MetaTable.class);
        String tableName = sqlDialect.wrapTableName(metaTable.getTableName());
        if (StrUtil.isNotBlank(metaTable.getDbName())) {
            tableName = String.format("%s.%s", metaTable.getDbName(), tableName);
        }
        try {
            PreparedStatement preparedStatement=connection.prepareStatement("DELETE FROM "+tableName+"WHERE "+sqlDialect.wrapFieldName(Constants.DEFAULT_JOB_MARKER_COLUMN)+"=?");
            preparedStatement.setLong(1,jobInfo.getId());
            preparedStatement.execute();
    
            preparedStatement=connection.prepareStatement("SELECT count(1) as totalCount FROM "+tableName+" WHERE "+sqlDialect.wrapFieldName(Constants.DEFAULT_JOB_MARKER_COLUMN)+" IS NOT NULL");
            ResultSet resultSet=preparedStatement.executeQuery();
            int totalCount=0;
            if(resultSet.next())
                totalCount=resultSet.getInt("totalCount");
            if (totalCount==0) {
                connection.createStatement().execute("ALTER TABLE " + sqlDialect.wrapTableName(jobInfo.getTableName()) + " DROP COLUMN "+sqlDialect.wrapFieldName(Constants.DEFAULT_JOB_MARKER_COLUMN));
            }
            PreparedStatement updateJob = systemConnection.prepareStatement("UPDATE job_info SET status=3 WHERE id=" + jobInfo.getId());
            updateJob.execute();
        } catch (SQLException e) {
            try {
                PreparedStatement updateJob = systemConnection.prepareStatement("UPDATE job_info SET status=2 WHERE id=" + jobInfo.getId());
                updateJob.execute();
                connection.close();
                systemConnection.close();
            } catch (SQLException ex) {
                throw new GenericException(ResponseCode.SQL_OPERATION_ERROR, ex.getMessage());
            }
            throw new GenericException(ResponseCode.SQL_OPERATION_ERROR, e.getMessage());
        }
        log.info("finish rollback,jobId:{}",jobInfo.getId());
        return true;
    }
}
