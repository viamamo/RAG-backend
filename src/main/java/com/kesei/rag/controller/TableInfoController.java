package com.kesei.rag.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kesei.rag.aop.AuthCheck;
import com.kesei.rag.entity.dto.GenericResponse;
import com.kesei.rag.entity.dto.table.TableInfoGetRequest;
import com.kesei.rag.entity.dto.table.TableInfoPostRequest;
import com.kesei.rag.entity.po.TableInfo;
import com.kesei.rag.entity.po.UserInfo;
import com.kesei.rag.exception.GenericException;
import com.kesei.rag.mocker.builder.SqlBuilder;
import com.kesei.rag.mocker.entity.MetaTable;
import com.kesei.rag.mocker.support.ResponseCode;
import com.kesei.rag.service.TableInfoService;
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
@RequestMapping("/table_info")
@Slf4j
public class TableInfoController {
    
    @Resource
    private TableInfoService tableInfoService;
    
    @Resource
    private UserInfoService userInfoService;
    
    
    // region 增删改查
    
    /**
     * 创建
     *
     * @param tableInfoPostRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public GenericResponse<Long> addTableInfo(@RequestBody TableInfoPostRequest tableInfoPostRequest,
                                              HttpServletRequest request) {
        if (tableInfoPostRequest == null) {
            throw new GenericException(ResponseCode.PARAMS_ERROR);
        }
        TableInfo tableInfo = new TableInfo();
        BeanUtils.copyProperties(tableInfoPostRequest, tableInfo);
        // 校验
        tableInfoService.valid(tableInfo, true);
        UserInfo currentUser = userInfoService.getCurrentUser(request);
        tableInfo.setUserId(currentUser.getId());
        boolean result = tableInfoService.save(tableInfo);
        if (!result) {
            throw new GenericException(ResponseCode.SQL_OPERATION_ERROR);
        }
        return ResponseUtils.success(tableInfo.getId());
    }
    
    /**
     * 删除
     *
     * @param tableInfoPostRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public GenericResponse<Boolean> deleteTableInfo(@RequestBody TableInfoPostRequest tableInfoPostRequest,
                                                    HttpServletRequest request) {
        if (tableInfoPostRequest == null || tableInfoPostRequest.getId() <= 0) {
            throw new GenericException(ResponseCode.PARAMS_ERROR);
        }
        UserInfo currentUser = userInfoService.getCurrentUser(request);
        long id = tableInfoPostRequest.getId();
        // 判断是否存在
        TableInfo oldTableInfo = tableInfoService.getById(id);
        if (oldTableInfo == null) {
            throw new GenericException(ResponseCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!oldTableInfo.getUserId().equals(currentUser.getId()) && !userInfoService.isAdmin(request)) {
            throw new GenericException(ResponseCode.UNAUTHORIZED_ERROR);
        }
        boolean b = tableInfoService.removeById(id);
        return ResponseUtils.success(b);
    }
    
    /**
     * 更新（仅管理员）
     *
     * @param tableInfoPostRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = Constants.ROLE_ADMIN)
    public GenericResponse<Boolean> updateTableInfo(@RequestBody TableInfoPostRequest tableInfoPostRequest) {
        if (tableInfoPostRequest == null || tableInfoPostRequest.getId() <= 0) {
            throw new GenericException(ResponseCode.PARAMS_ERROR);
        }
        TableInfo tableInfo = new TableInfo();
        BeanUtils.copyProperties(tableInfoPostRequest, tableInfo);
        // 参数校验
        tableInfoService.valid(tableInfo, false);
        long id = tableInfoPostRequest.getId();
        // 判断是否存在
        TableInfo oldTableInfo = tableInfoService.getById(id);
        if (oldTableInfo == null) {
            throw new GenericException(ResponseCode.NOT_FOUND_ERROR);
        }
        boolean result = tableInfoService.updateById(tableInfo);
        return ResponseUtils.success(result);
    }
    
    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public GenericResponse<TableInfo> getTableInfoById(long id) {
        if (id <= 0) {
            throw new GenericException(ResponseCode.PARAMS_ERROR);
        }
        TableInfo tableInfo = tableInfoService.getById(id);
        return ResponseUtils.success(tableInfo);
    }
    
    /**
     * 获取列表（仅管理员可使用）
     *
     * @param tableInfoGetRequest
     * @return
     */
    @AuthCheck(mustRole = Constants.ROLE_ADMIN)
    @GetMapping("/list")
    public GenericResponse<List<TableInfo>> listTableInfo(TableInfoGetRequest tableInfoGetRequest) {
        List<TableInfo> tableInfoList = tableInfoService.list(getQueryWrapper(tableInfoGetRequest));
        return ResponseUtils.success(tableInfoList);
    }
    
    /**
     * 分页获取列表
     *
     * @param tableInfoGetRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page")
    public GenericResponse<Page<TableInfo>> listTableInfoByPage(TableInfoGetRequest tableInfoGetRequest,
                                                             HttpServletRequest request) {
        long pageNum = tableInfoGetRequest.getPaginationNum();
        long pageSize = tableInfoGetRequest.getPaginationSize();
        Page<TableInfo> tableInfoPage = tableInfoService.page(new Page<>(pageNum, pageSize),
                getQueryWrapper(tableInfoGetRequest));
        return ResponseUtils.success(tableInfoPage);
    }
    
    /**
     * 获取当前用户可选的全部资源列表（只返回 id 和名称）
     *
     * @param tableInfoGetRequest
     * @param request
     * @return
     */
    @GetMapping("/my/list")
    public GenericResponse<List<TableInfo>> listMyTableInfo(TableInfoGetRequest tableInfoGetRequest,
                                                         HttpServletRequest request) {
        TableInfo tableInfoQuery = new TableInfo();
        if (tableInfoGetRequest == null) {
            throw new GenericException(ResponseCode.PARAMS_ERROR);
        }
        BeanUtils.copyProperties(tableInfoGetRequest, tableInfoQuery);
        QueryWrapper<TableInfo> queryWrapper = getQueryWrapper(tableInfoGetRequest);
        final String[] fields = new String[]{"id", "name"};
        queryWrapper.select(fields);
        List<TableInfo> tableInfoList = tableInfoService.list(queryWrapper);
        try {
            UserInfo currentUser = userInfoService.getCurrentUser(request);
            tableInfoQuery.setUserId(currentUser.getId());
            queryWrapper = new QueryWrapper<>(tableInfoQuery);
            queryWrapper.select(fields);
            tableInfoList.addAll(tableInfoService.list(queryWrapper));
        } catch (Exception e) {
            // 未登录
        }
        // 根据 id 去重
        List<TableInfo> resultList = tableInfoList.stream().collect(Collectors.collectingAndThen(
                Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(TableInfo::getId))), ArrayList::new));
        return ResponseUtils.success(resultList);
    }
    
    /**
     * 分页获取当前用户可选的资源列表
     *
     * @param tableInfoGetRequest
     * @param request
     * @return
     */
    @GetMapping("/my/list/page")
    public GenericResponse<Page<TableInfo>> listMyTableInfoByPage(TableInfoGetRequest tableInfoGetRequest,
                                                               HttpServletRequest request) {
        UserInfo currentUser = userInfoService.getCurrentUser(request);
        long pageNum = tableInfoGetRequest.getPaginationNum();
        long pageSize = tableInfoGetRequest.getPaginationSize();
        QueryWrapper<TableInfo> queryWrapper = getQueryWrapper(tableInfoGetRequest);
        queryWrapper.eq("userId", currentUser.getId());
        Page<TableInfo> tableInfoPage = tableInfoService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return ResponseUtils.success(tableInfoPage);
    }
    
    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param tableInfoGetRequest
     * @param request
     * @return
     */
    @GetMapping("/my/add/list/page")
    public GenericResponse<Page<TableInfo>> listMyAddTableInfoByPage(TableInfoGetRequest tableInfoGetRequest,
                                                                  HttpServletRequest request) {
        if (tableInfoGetRequest == null) {
            throw new GenericException(ResponseCode.PARAMS_ERROR);
        }
        UserInfo currentUser = userInfoService.getCurrentUser(request);
        tableInfoGetRequest.setUserId(currentUser.getId());
        long pageNum = tableInfoGetRequest.getPaginationNum();
        long pageSize = tableInfoGetRequest.getPaginationSize();
        Page<TableInfo> tableInfoPage = tableInfoService.page(new Page<>(pageNum, pageSize),
                getQueryWrapper(tableInfoGetRequest));
        return ResponseUtils.success(tableInfoPage);
    }
    
    /**
     * 生成创建表的 SQL
     *
     * @param id
     * @return
     */
    @PostMapping("/generate/sql")
    public GenericResponse<String> generateCreateSql(@RequestBody long id) {
        if (id <= 0) {
            throw new GenericException(ResponseCode.PARAMS_ERROR);
        }
        TableInfo tableInfo = tableInfoService.getById(id);
        if (tableInfo == null) {
            throw new GenericException(ResponseCode.NOT_FOUND_ERROR);
        }
        MetaTable metaTable = JSONUtil.toBean(tableInfo.getContent(), MetaTable.class);
        SqlBuilder sqlBuilder = new SqlBuilder();
        return ResponseUtils.success(sqlBuilder.buildCreateTableSql(metaTable));
    }
    
    /**
     * 获取查询包装类
     *
     * @param tableInfoGetRequest
     * @return
     */
    private QueryWrapper<TableInfo> getQueryWrapper(TableInfoGetRequest tableInfoGetRequest) {
        if (tableInfoGetRequest == null) {
            throw new GenericException(ResponseCode.PARAMS_ERROR, "请求参数为空");
        }
        TableInfo tableInfoQuery = new TableInfo();
        BeanUtils.copyProperties(tableInfoGetRequest, tableInfoQuery);
        String sortColumn = tableInfoGetRequest.getSortColumn();
        String sortOrder = tableInfoGetRequest.getSortOrder();
        String searchParam=tableInfoGetRequest.getSearchParam();
        tableInfoQuery.setName(null);
        tableInfoQuery.setContent(null);
        QueryWrapper<TableInfo> queryWrapper = new QueryWrapper<>(tableInfoQuery);
        queryWrapper.like(StrUtil.isNotBlank(searchParam), "name", searchParam);
        queryWrapper.orderBy(StrUtil.isNotBlank(sortColumn), sortOrder.equals(Constants.SORT_ORDER_ASC),
                sortColumn);
        return queryWrapper;
    }
}
