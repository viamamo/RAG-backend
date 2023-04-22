package com.kesei.rag.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kesei.rag.entity.po.DbInfo;
import com.kesei.rag.entity.po.JobInfo;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author kesei
 */
public interface JobInfoService extends IService<JobInfo> {
    
    Connection getSystemConnection() throws SQLException;
    Connection getConnection(DbInfo dbInfo);
    
    /**
     * 处理作业添加
     * @param jobInfo
     * @param metaTableString
     * @param dbInfo
     */
    void handleAdd(JobInfo jobInfo,String metaTableString, DbInfo dbInfo);
    
    /**
     * 执行简单作业
     * @param connection
     * @param sqlString
     * @return
     */
    Boolean executeSimpleSql(Connection connection, String sqlString);
    
    /**
     * 执行作业
     * @param jobInfo
     * @param connection
     */
    void executeJob(JobInfo jobInfo,Connection systemConnection,Connection connection);
    
    boolean rollbackJob(JobInfo jobInfo,Connection systemConnection,Connection connection);
}
