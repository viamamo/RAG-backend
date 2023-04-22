package com.kesei.rag.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
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
import com.kesei.rag.mocker.entity.MetaTable;
import com.kesei.rag.mocker.support.ResponseCode;
import com.kesei.rag.service.DbInfoService;
import com.kesei.rag.service.JobInfoService;
import com.kesei.rag.service.UserInfoService;
import com.kesei.rag.support.Constants;
import com.kesei.rag.support.utils.ResponseUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.providers.base.Job;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author kesei
 */
@RestController
@RequestMapping("job_info")
@Slf4j
public class JobInfoController {
    
    @Resource
    UserInfoService userInfoService;
    @Resource
    DbInfoService dbInfoService;
    @Resource
    JobInfoService jobInfoService;
    
    @RequestMapping("/add")
    public GenericResponse<Long> addJob(@RequestBody AddJobRequest addJobRequest, HttpServletRequest request){
        JobInfo jobInfo=new JobInfo();
        UserInfo currentUser = userInfoService.getCurrentUser(request);
        DbInfo dbInfo=dbInfoService.getById(addJobRequest.getDbInfoId());
        
        jobInfo.setUserId(currentUser.getId());
        jobInfoService.handleAdd(jobInfo, addJobRequest.getContent(), dbInfo);
        boolean result = jobInfoService.save(jobInfo);
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
        JobInfo oldJobInfo = jobInfoService.getById(id);
        if (oldJobInfo == null) {
            throw new GenericException(ResponseCode.NOT_FOUND_ERROR);
        }
        return ResponseUtils.success(jobInfoService.removeById(id));
    }
    
    @GetMapping("/list/page")
    public GenericResponse<Page<JobInfo>> listDbInfoByPage(JobInfoGetRequest jobInfoGetRequest) {
        long pageNum = jobInfoGetRequest.getPaginationNum();
        long pageSize = jobInfoGetRequest.getPaginationSize();
        Page<JobInfo> jobInfoPage = jobInfoService.page(new Page<>(pageNum, pageSize),
                getQueryWrapper(jobInfoGetRequest));
        return ResponseUtils.success(jobInfoPage);
    }
    
    @RequestMapping("/execute")
    public GenericResponse<Boolean> executeJob(@RequestBody JobInfoPostRequest jobInfoPostRequest) {
        JobInfo jobInfo=jobInfoService.getById(jobInfoPostRequest.getId());
        Connection connection= jobInfoService.getConnection(dbInfoService.getById(jobInfo.getDbId()));
        try {
            Connection systemConnection = jobInfoService.getSystemConnection();
            jobInfoService.executeJob(jobInfo,systemConnection, connection);
        } catch (SQLException e) {
            return ResponseUtils.error(ResponseCode.SYSTEM_ERROR,"获取系统线程失败");
        }
        return ResponseUtils.success(true);
    }
    
    @RequestMapping("/rollback")
    public GenericResponse<Boolean> rollbackJob(@RequestBody JobInfoPostRequest jobInfoPostRequest){
        JobInfo jobInfo=jobInfoService.getById(jobInfoPostRequest.getId());
        Connection connection= jobInfoService.getConnection(dbInfoService.getById(jobInfo.getDbId()));
        try {
            Connection systemConnection = jobInfoService.getSystemConnection();
            return ResponseUtils.success(jobInfoService.rollbackJob(jobInfo,systemConnection, connection));
        } catch (SQLException e) {
            return ResponseUtils.error(ResponseCode.SYSTEM_ERROR,"获取系统线程失败");
        }
    }
    
    @RequestMapping("/execute/simple")
    public GenericResponse<Boolean> executeSqlSimple(@RequestBody SimpleExecutionRequest simpleExecutionRequest){
        DbInfo dbInfo=dbInfoService.getById(simpleExecutionRequest.getDbInfoId());
        Connection connection= jobInfoService.getConnection(dbInfo);
        return ResponseUtils.success(
                StrUtil.isNotBlank(simpleExecutionRequest.getSql())
                        ?jobInfoService.executeSimpleSql(connection, simpleExecutionRequest.getSql())
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
