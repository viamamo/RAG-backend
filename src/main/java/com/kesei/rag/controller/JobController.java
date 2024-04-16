package com.kesei.rag.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kesei.rag.entity.dto.GenericResponse;
import com.kesei.rag.entity.dto.job.AddJobRequest;
import com.kesei.rag.entity.dto.job.JobGetRequest;
import com.kesei.rag.entity.dto.job.JobPostRequest;
import com.kesei.rag.entity.dto.job.SimpleExecutionRequest;
import com.kesei.rag.entity.po.DbInfo;
import com.kesei.rag.entity.po.JobInfo;
import com.kesei.rag.entity.po.UserInfo;
import com.kesei.rag.exception.GenericException;
import com.kesei.rag.mocker.support.ResponseCode;
import com.kesei.rag.mocker.support.utils.MockTool;
import com.kesei.rag.service.DbInfoService;
import com.kesei.rag.service.UserInfoService;
import com.kesei.rag.service.impl.JobServiceImpl;
import com.kesei.rag.support.Constants;
import com.kesei.rag.support.utils.ResponseUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author viamamo
 */
@RestController
@RequestMapping("job")
@Slf4j
public class JobController {
    
    @Resource
    UserInfoService userInfoService;
    @Resource
    DbInfoService dbInfoService;
    @Resource
    JobServiceImpl jobService;
    
    /**
     * 添加
     *
     * @param jobPostRequest post封装
     * @return jobId
     */
    @RequestMapping("/add")
    public GenericResponse<Long> addJob(@RequestBody JobPostRequest jobPostRequest, HttpServletRequest request){
        JobInfo jobInfo=new JobInfo();
        UserInfo currentUser = userInfoService.getCurrentUser(request);
        DbInfo dbInfo=dbInfoService.getById(jobPostRequest.getDbInfoId());
        
        jobInfo.setUserId(currentUser.getId());
        jobService.handleAdd(jobInfo, jobPostRequest.getContent(), dbInfo);
        boolean result = jobService.save(jobInfo);
        if (!result) {
            throw new GenericException(ResponseCode.SQL_OPERATION_ERROR);
        }
        return ResponseUtils.success(jobInfo.getId());
    }
    
    /**
     * 删除
     *
     * @param jobPostRequest post封装
     * @return 删除是否成功
     */
    @PostMapping("/delete")
    public GenericResponse<Boolean> deleteJob(@RequestBody JobPostRequest jobPostRequest) {
        if (jobPostRequest == null || jobPostRequest.getId() <= 0) {
            throw new GenericException(ResponseCode.PARAMS_ERROR);
        }
        long id = jobPostRequest.getId();
        // 判断是否存在
        JobInfo oldJobInfo = jobService.getById(id);
        if (oldJobInfo == null) {
            throw new GenericException(ResponseCode.NOT_FOUND_ERROR);
        }
        return ResponseUtils.success(jobService.removeById(id));
    }
    
    /**
     * 分页获取列表
     *
     * @param jobGetRequest get封装
     * @return 分页
     */
    
    @GetMapping("/list/page")
    public GenericResponse<Page<JobInfo>> listJobByPage(JobGetRequest jobGetRequest) {
        long pageNum = jobGetRequest.getPaginationNum();
        long pageSize = jobGetRequest.getPaginationSize();
        Page<JobInfo> jobInfoPage = jobService.page(new Page<>(pageNum, pageSize),
                getQueryWrapper(jobGetRequest));
        jobInfoPage.getRecords().forEach((record)-> record.setDbType(MockTool.getDatabaseTypeByStr(record.getDbType()).getName()));
        return ResponseUtils.success(jobInfoPage);
    }
    
    /**
     * 执行作业
     *
     * @param jobPostRequest post封装
     * @return 作业启动结果
     */
    
    @RequestMapping("/execute")
    public GenericResponse<Boolean> executeJob(@RequestBody JobPostRequest jobPostRequest) {
        JobInfo jobInfo= jobService.getById(jobPostRequest.getId());
        DbInfo dbInfo=dbInfoService.getById(jobInfo.getDbId());
        Connection connection= jobService.getConnection(dbInfoService.getById(jobInfo.getDbId()));
        try {
            Connection systemConnection = jobService.getSystemConnection();
            jobService.executeJob(jobInfo,systemConnection, connection);
        } catch (SQLException e) {
            try {
                connection.close();
            } catch (SQLException ex) {
                throw new GenericException(ResponseCode.SQL_OPERATION_ERROR);
            }
            return ResponseUtils.error(ResponseCode.SYSTEM_ERROR,"获取系统线程失败");
        }
        return ResponseUtils.success(true);
    }
    
    /**
     * 回滚作业
     *
     * @param jobPostRequest post封装
     * @return 作业回滚结果
     */
    @RequestMapping("/rollback")
    public GenericResponse<Boolean> rollbackJob(@RequestBody JobPostRequest jobPostRequest){
        JobInfo jobInfo= jobService.getById(jobPostRequest.getId());
        Connection connection= jobService.getConnection(dbInfoService.getById(jobInfo.getDbId()));
        try {
            Connection systemConnection = jobService.getSystemConnection();
            return ResponseUtils.success(jobService.rollbackJob(jobInfo,systemConnection, connection));
        } catch (SQLException e) {
            return ResponseUtils.error(ResponseCode.SYSTEM_ERROR,"获取系统线程失败");
        }
    }
    
    /**
     * 同步执行sql
     *
     * @param simpleExecutionRequest post封装
     * @return 执行结果
     */
    @RequestMapping("/execute/simple")
    public GenericResponse<Boolean> executeSqlSimple(@RequestBody SimpleExecutionRequest simpleExecutionRequest){
        DbInfo dbInfo=dbInfoService.getById(simpleExecutionRequest.getDbInfoId());
        Connection connection= jobService.getConnection(dbInfo);
        return ResponseUtils.success(
                StrUtil.isNotBlank(simpleExecutionRequest.getSql())
                        ? jobService.executeSimpleSql(connection, simpleExecutionRequest.getSql())
                        :true
        );
    }
    
    /**
     * 获取查询包装类
     *
     * @param jobGetRequest get封装
     * @return mb+查询包装
     */
    private QueryWrapper<JobInfo> getQueryWrapper(JobGetRequest jobGetRequest) {
        if (jobGetRequest == null) {
            throw new GenericException(ResponseCode.PARAMS_ERROR, "请求参数为空");
        }
        JobInfo jobInfo = new JobInfo();
        BeanUtils.copyProperties(jobGetRequest, jobInfo);
        String sortColumn = jobGetRequest.getSortColumn();
        String sortOrder = jobGetRequest.getSortOrder();
        String searchParam = jobGetRequest.getSearchParam();
        jobInfo.setTableName(null);
        jobInfo.setDbName(null);
        QueryWrapper<JobInfo> queryWrapper = new QueryWrapper<>(jobInfo);
        queryWrapper.like(StrUtil.isNotBlank(searchParam), "tableName", searchParam)
                .or()
                .like(StrUtil.isNotBlank(searchParam),"dbName",searchParam);
        queryWrapper.orderBy(StrUtil.isNotBlank(sortColumn), sortOrder.equals(Constants.SORT_ORDER_ASC),
                sortColumn);
        return queryWrapper;
    }
}
