package com.kesei.rag.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kesei.rag.entity.dto.GenericResponse;
import com.kesei.rag.entity.dto.db.DbInfoGetRequest;
import com.kesei.rag.entity.dto.db.DbInfoPostRequest;
import com.kesei.rag.entity.po.DbInfo;
import com.kesei.rag.entity.po.UserInfo;
import com.kesei.rag.exception.GenericException;
import com.kesei.rag.mocker.support.ResponseCode;
import com.kesei.rag.service.DbInfoService;
import com.kesei.rag.service.UserInfoService;
import com.kesei.rag.support.Constants;
import com.kesei.rag.support.utils.ResponseUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * @author kesei
 */
@RestController
@RequestMapping("/db_info")
@Slf4j
public class DbInfoController{
    
    @Resource
    private DbInfoService dbInfoService;
    
    @Resource
    private UserInfoService userInfoService;
    
    @RequestMapping("/add")
    public GenericResponse<Long> addDbInfo(@RequestBody DbInfoPostRequest dbInfoPostRequest, HttpServletRequest request){
        if (dbInfoPostRequest == null) {
            throw new GenericException(ResponseCode.PARAMS_ERROR);
        }
        DbInfo dbInfo = new DbInfo();
        BeanUtils.copyProperties(dbInfoPostRequest, dbInfo);
        // 校验
        dbInfoService.valid(dbInfo, true);
        UserInfo currentUser = userInfoService.getCurrentUser(request);
        dbInfo.setUserId(currentUser.getId());
        boolean result = dbInfoService.save(dbInfo);
        if (!result) {
            throw new GenericException(ResponseCode.SQL_OPERATION_ERROR);
        }
        return ResponseUtils.success(dbInfo.getId());
    }
    
    /**
     * 删除
     *
     * @param dbInfoPostRequest
     * @return
     */
    @PostMapping("/delete")
    public GenericResponse<Boolean> deleteDbInfo(@RequestBody DbInfoPostRequest dbInfoPostRequest) {
        if (dbInfoPostRequest == null || dbInfoPostRequest.getId() <= 0) {
            throw new GenericException(ResponseCode.PARAMS_ERROR);
        }
        long id = dbInfoPostRequest.getId();
        // 判断是否存在
        DbInfo oldDbInfo = dbInfoService.getById(id);
        if (oldDbInfo == null) {
            throw new GenericException(ResponseCode.NOT_FOUND_ERROR);
        }
        boolean b = dbInfoService.removeById(id);
        return ResponseUtils.success(b);
    }
    
    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public GenericResponse<DbInfo> getDbInfoById(long id) {
        if (id <= 0) {
            throw new GenericException(ResponseCode.PARAMS_ERROR);
        }
        DbInfo dbInfo = dbInfoService.getById(id);
        return ResponseUtils.success(dbInfo);
    }
    
    /**
     * 获取列表
     *
     * @param dbInfoGetRequest
     * @return
     */
    @GetMapping("/list")
    public GenericResponse<List<DbInfo>> listDbInfo(DbInfoGetRequest dbInfoGetRequest) {
        List<DbInfo> dbInfoList = dbInfoService.list(getQueryWrapper(dbInfoGetRequest));
        return ResponseUtils.success(dbInfoList);
    }
    
    /**
     * 分页获取列表
     *
     * @param dbInfoGetRequest
     * @return
     */
    @GetMapping("/list/page")
    public GenericResponse<Page<DbInfo>> listDbInfoByPage(DbInfoGetRequest dbInfoGetRequest) {
        long pageNum = dbInfoGetRequest.getPaginationNum();
        long pageSize = dbInfoGetRequest.getPaginationSize();
        Page<DbInfo> dbInfoPage = dbInfoService.page(new Page<>(pageNum, pageSize),
                getQueryWrapper(dbInfoGetRequest));
        return ResponseUtils.success(dbInfoPage);
    }
    
    /**
     * 获取当前用户可选的全部资源列表（只返回 id 和名称）
     *
     * @param dbInfoGetRequest
     * @param request
     * @return
     */
    @GetMapping("/my/list")
    public GenericResponse<List<DbInfo>> listMyDbInfo(DbInfoGetRequest dbInfoGetRequest,
                                                            HttpServletRequest request) {
        DbInfo dbInfoQuery = new DbInfo();
        if (dbInfoGetRequest == null) {
            throw new GenericException(ResponseCode.PARAMS_ERROR);
        }
        BeanUtils.copyProperties(dbInfoGetRequest, dbInfoQuery);
        QueryWrapper<DbInfo> queryWrapper = getQueryWrapper(dbInfoGetRequest);
        final String[] fields = new String[]{"id", "name"};
        queryWrapper.select(fields);
        List<DbInfo> dbInfoList = dbInfoService.list(queryWrapper);
        try {
            UserInfo currentUser = userInfoService.getCurrentUser(request);
            dbInfoQuery.setUserId(currentUser.getId());
            queryWrapper = new QueryWrapper<>(dbInfoQuery);
            queryWrapper.select(fields);
            dbInfoList.addAll(dbInfoService.list(queryWrapper));
        } catch (Exception e) {
            // 未登录
        }
        // 根据 id 去重
        List<DbInfo> resultList = dbInfoList.stream().collect(Collectors.collectingAndThen(
                Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(DbInfo::getId))), ArrayList::new));
        return ResponseUtils.success(resultList);
    }
    
    /**
     * 分页获取当前用户可选的资源列表
     *
     * @param dbInfoGetRequest
     * @param request
     * @return
     */
    @GetMapping("/my/list/page")
    public GenericResponse<Page<DbInfo>> listMyDbInfoByPage(DbInfoGetRequest dbInfoGetRequest,
                                                                  HttpServletRequest request) {
        UserInfo currentUser = userInfoService.getCurrentUser(request);
        long pageNum = dbInfoGetRequest.getPaginationNum();
        long pageSize = dbInfoGetRequest.getPaginationSize();
        QueryWrapper<DbInfo> queryWrapper = getQueryWrapper(dbInfoGetRequest);
        queryWrapper.eq("userId", currentUser.getId());
        Page<DbInfo> dbInfoPage = dbInfoService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return ResponseUtils.success(dbInfoPage);
    }
    
    /**
     * 获取查询包装类
     *
     * @param dbInfoGetRequest
     * @return
     */
    private QueryWrapper<DbInfo> getQueryWrapper(DbInfoGetRequest dbInfoGetRequest) {
        if (dbInfoGetRequest == null) {
            throw new GenericException(ResponseCode.PARAMS_ERROR, "请求参数为空");
        }
        DbInfo dbInfoQuery = new DbInfo();
        BeanUtils.copyProperties(dbInfoGetRequest, dbInfoQuery);
        String sortColumn = dbInfoGetRequest.getSortColumn();
        String sortOrder = dbInfoGetRequest.getSortOrder();
        String searchParam = dbInfoGetRequest.getSearchParam();
        dbInfoQuery.setName(null);
        QueryWrapper<DbInfo> queryWrapper = new QueryWrapper<>(dbInfoQuery);
        queryWrapper.like(StrUtil.isNotBlank(searchParam), "name", searchParam);
        queryWrapper.orderBy(StrUtil.isNotBlank(sortColumn), sortOrder.equals(Constants.SORT_ORDER_ASC),
                sortColumn);
        return queryWrapper;
    }
}
