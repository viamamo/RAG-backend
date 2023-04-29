package com.kesei.rag.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kesei.rag.entity.dto.GenericResponse;
import com.kesei.rag.entity.dto.job.AddJobRequest;
import com.kesei.rag.entity.dto.job.JobInfoGetRequest;
import com.kesei.rag.entity.dto.job.JobInfoPostRequest;
import com.kesei.rag.entity.dto.job.SimpleExecutionRequest;
import com.kesei.rag.entity.po.DbInfo;
import com.kesei.rag.entity.po.JobInfo;
import com.kesei.rag.entity.po.UserInfo;
import com.kesei.rag.exception.GenericException;
import com.kesei.rag.mocker.support.ResponseCode;
import com.kesei.rag.service.DbInfoService;
import com.kesei.rag.service.JobService;
import com.kesei.rag.service.UserInfoService;
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
 * @author kesei
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
    JobService jobService;
    
    @RequestMapping("/add")
    public GenericResponse<Long> addJob(@RequestBody AddJobRequest addJobRequest, HttpServletRequest request){
        JobInfo jobInfo=new JobInfo();
        UserInfo currentUser = userInfoService.getCurrentUser(request);
        DbInfo dbInfo=dbInfoService.getById(addJobRequest.getDbInfoId());
        
        jobInfo.setUserId(currentUser.getId());
        jobService.handleAdd(jobInfo, addJobRequest.getContent(), dbInfo);
        boolean result = jobService.save(jobInfo);
        if (!result) {
            throw new GenericException(ResponseCode.SQL_OPERATION_ERROR);
        }
        return ResponseUtils.success(jobInfo.getId());
    }
    
    @PostMapping("/delete")
    public GenericResponse<Boolean> deleteDbInfo(@RequestBody JobInfoPostRequest jobInfoPostRequest) {
        if (jobInfoPostRequest == null || jobInfoPostRequest.getId() <= 0) {
            throw new GenericException(ResponseCode.PARAMS_ERROR);
        }
        long id = jobInfoPostRequest.getId();
        // 判断是否存在
        JobInfo oldJobInfo = jobService.getById(id);
        if (oldJobInfo == null) {
            throw new GenericException(ResponseCode.NOT_FOUND_ERROR);
        }
        return ResponseUtils.success(jobService.removeById(id));
    }
    
    @GetMapping("/list/page")
    public GenericResponse<Page<JobInfo>> listDbInfoByPage(JobInfoGetRequest jobInfoGetRequest) {
        long pageNum = jobInfoGetRequest.getPaginationNum();
        long pageSize = jobInfoGetRequest.getPaginationSize();
        Page<JobInfo> jobInfoPage = jobService.page(new Page<>(pageNum, pageSize),
                getQueryWrapper(jobInfoGetRequest));
        return ResponseUtils.success(jobInfoPage);
    }
    
    @RequestMapping("/execute")
    public GenericResponse<Boolean> executeJob(@RequestBody JobInfoPostRequest jobInfoPostRequest) {
        JobInfo jobInfo= jobService.getById(jobInfoPostRequest.getId());
        Connection connection= jobService.getConnection(dbInfoService.getById(jobInfo.getDbId()));
        try {
            Connection systemConnection = jobService.getSystemConnection();
            jobService.executeJob(jobInfo,systemConnection, connection);
        } catch (SQLException e) {
            return ResponseUtils.error(ResponseCode.SYSTEM_ERROR,"获取系统线程失败");
        }
        return ResponseUtils.success(true);
    }
    
    @RequestMapping("/rollback")
    public GenericResponse<Boolean> rollbackJob(@RequestBody JobInfoPostRequest jobInfoPostRequest){
        JobInfo jobInfo= jobService.getById(jobInfoPostRequest.getId());
        Connection connection= jobService.getConnection(dbInfoService.getById(jobInfo.getDbId()));
        try {
            Connection systemConnection = jobService.getSystemConnection();
            return ResponseUtils.success(jobService.rollbackJob(jobInfo,systemConnection, connection));
        } catch (SQLException e) {
            return ResponseUtils.error(ResponseCode.SYSTEM_ERROR,"获取系统线程失败");
        }
    }
    
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
    
    private QueryWrapper<JobInfo> getQueryWrapper(JobInfoGetRequest jobInfoGetRequest) {
        if (jobInfoGetRequest == null) {
            throw new GenericException(ResponseCode.PARAMS_ERROR, "请求参数为空");
        }
        JobInfo jobInfo = new JobInfo();
        BeanUtils.copyProperties(jobInfoGetRequest, jobInfo);
        String sortColumn = jobInfoGetRequest.getSortColumn();
        String sortOrder = jobInfoGetRequest.getSortOrder();
        String searchParam = jobInfoGetRequest.getSearchParam();
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
