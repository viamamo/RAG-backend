package com.kesei.rag.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kesei.rag.entity.po.DbInfo;
import com.kesei.rag.entity.po.JobInfo;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author kesei
 */
public interface JobService extends IService<JobInfo> {
    
    /**
     * 获取系统数据库连接
     *
     * @return 系统数据库连接
     */
    Connection getSystemConnection() throws SQLException;
    
    /**
     * 获取目标数据库连接
     * @param dbInfo DbInfo
     * @return 目标数据库连接
     */
    Connection getConnection(DbInfo dbInfo);
    
    /**
     * 处理作业添加
     *
     * @param jobInfo jobInfo
     * @param metaTableString MetaTable JSON
     * @param dbInfo 目标数据库
     */
    void handleAdd(JobInfo jobInfo,String metaTableString, DbInfo dbInfo);
    
    /**
     * 执行简单作业
     *
     * @param connection 目标数据库连接
     * @param sqlString sql
     * @return 执行结果
     */
    Boolean executeSimpleSql(Connection connection, String sqlString);
    
    /**
     * 执行作业
     *
     * @param jobInfo JobInfo
     * @param connection 目标数据库连接
     */
    void executeJob(JobInfo jobInfo,Connection systemConnection,Connection connection);
    
    /**
     * 回滚作业
     *
     * @param jobInfo JobInfo
     * @param systemConnection 系统数据库连接
     * @param connection 目标数据库连接
     * @return 回滚结果
     */
    boolean rollbackJob(JobInfo jobInfo,Connection systemConnection,Connection connection);
}
