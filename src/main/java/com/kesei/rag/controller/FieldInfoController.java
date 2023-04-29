package com.kesei.rag.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kesei.rag.aop.AuthCheck;
import com.kesei.rag.entity.dto.GenericResponse;
import com.kesei.rag.entity.dto.field.FieldInfoGetRequest;
import com.kesei.rag.entity.dto.field.FieldInfoPostRequest;
import com.kesei.rag.entity.po.FieldInfo;
import com.kesei.rag.entity.po.UserInfo;
import com.kesei.rag.exception.GenericException;
import com.kesei.rag.mocker.builder.SqlBuilder;
import com.kesei.rag.mocker.entity.MetaTable;
import com.kesei.rag.mocker.support.ResponseCode;
import com.kesei.rag.service.FieldInfoService;
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
@RequestMapping("/field_info")
@Slf4j
public class FieldInfoController {
    
    @Resource
    private FieldInfoService fieldInfoService;
    
    @Resource
    private UserInfoService userInfoService;
    
    /**
     * 创建
     *
     * @param fieldInfoPostRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public GenericResponse<Long> addFieldInfo(@RequestBody FieldInfoPostRequest fieldInfoPostRequest,
                                              HttpServletRequest request) {
        if (fieldInfoPostRequest == null) {
            throw new GenericException(ResponseCode.PARAMS_ERROR);
        }
        FieldInfo fieldInfo = new FieldInfo();
        BeanUtils.copyProperties(fieldInfoPostRequest, fieldInfo);
        // 校验
        fieldInfoService.valid(fieldInfo, true);
        UserInfo currentUser = userInfoService.getCurrentUser(request);
        fieldInfo.setUserId(currentUser.getId());
        boolean result = fieldInfoService.save(fieldInfo);
        if (!result) {
            throw new GenericException(ResponseCode.SQL_OPERATION_ERROR);
        }
        return ResponseUtils.success(fieldInfo.getId());
    }
    
    /**
     * 删除
     *
     * @param fieldInfoPostRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public GenericResponse<Boolean> deleteFieldInfo(@RequestBody FieldInfoPostRequest fieldInfoPostRequest,
                                                    HttpServletRequest request) {
        if (fieldInfoPostRequest == null || fieldInfoPostRequest.getId() <= 0) {
            throw new GenericException(ResponseCode.PARAMS_ERROR);
        }
        UserInfo currentUser = userInfoService.getCurrentUser(request);
        long id = fieldInfoPostRequest.getId();
        // 判断是否存在
        FieldInfo oldFieldInfo = fieldInfoService.getById(id);
        if (oldFieldInfo == null) {
            throw new GenericException(ResponseCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!oldFieldInfo.getUserId().equals(currentUser.getId()) && !userInfoService.isAdmin(request)) {
            throw new GenericException(ResponseCode.UNAUTHORIZED_ERROR);
        }
        boolean b = fieldInfoService.removeById(id);
        return ResponseUtils.success(b);
    }
    
    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public GenericResponse<FieldInfo> getFieldInfoById(long id) {
        if (id <= 0) {
            throw new GenericException(ResponseCode.PARAMS_ERROR);
        }
        FieldInfo fieldInfo = fieldInfoService.getById(id);
        return ResponseUtils.success(fieldInfo);
    }
    
    /**
     * 获取列表（仅管理员可使用）
     *
     * @param fieldInfoQueryRequest
     * @return
     */
    @AuthCheck(mustRole = Constants.ROLE_ADMIN)
    @GetMapping("/list")
    public GenericResponse<List<FieldInfo>> listFieldInfo(FieldInfoGetRequest fieldInfoQueryRequest) {
        List<FieldInfo> fieldInfoList = fieldInfoService.list(getQueryWrapper(fieldInfoQueryRequest));
        return ResponseUtils.success(fieldInfoList);
    }
    
    /**
     * 分页获取列表
     *
     * @param fieldInfoGetRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page")
    public GenericResponse<Page<FieldInfo>> listFieldInfoByPage(FieldInfoGetRequest fieldInfoGetRequest,
                                                             HttpServletRequest request) {
        long pageNum = fieldInfoGetRequest.getPaginationNum();
        long pageSize = fieldInfoGetRequest.getPaginationSize();
        Page<FieldInfo> fieldInfoPage = fieldInfoService.page(new Page<>(pageNum, pageSize),
                getQueryWrapper(fieldInfoGetRequest));
        return ResponseUtils.success(fieldInfoPage);
    }
    
    /**
     * 获取当前用户可选的全部资源列表（只返回 id 和名称）
     *
     * @param fieldInfoGetRequest
     * @param request
     * @return
     */
    @GetMapping("/my/list")
    public GenericResponse<List<FieldInfo>> listMyFieldInfo(FieldInfoGetRequest fieldInfoGetRequest,
                                                         HttpServletRequest request) {
        FieldInfo fieldInfoQuery = new FieldInfo();
        if (fieldInfoGetRequest == null) {
            throw new GenericException(ResponseCode.PARAMS_ERROR);
        }
        BeanUtils.copyProperties(fieldInfoGetRequest, fieldInfoQuery);
        QueryWrapper<FieldInfo> queryWrapper = getQueryWrapper(fieldInfoGetRequest);
        final String[] fields = new String[]{"id", "name"};
        queryWrapper.select(fields);
        List<FieldInfo> fieldInfoList = fieldInfoService.list(queryWrapper);
        try {
            UserInfo currentUser = userInfoService.getCurrentUser(request);
            fieldInfoQuery.setUserId(currentUser.getId());
            queryWrapper = new QueryWrapper<>(fieldInfoQuery);
            queryWrapper.select(fields);
            fieldInfoList.addAll(fieldInfoService.list(queryWrapper));
        } catch (Exception e) {
            // 未登录
        }
        // 根据 id 去重
        List<FieldInfo> resultList = fieldInfoList.stream().collect(Collectors.collectingAndThen(
                Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(FieldInfo::getId))), ArrayList::new));
        return ResponseUtils.success(resultList);
    }
    
    /**
     * 分页获取当前用户可选的资源列表
     *
     * @param fieldInfoGetRequest
     * @param request
     * @return
     */
    @GetMapping("/my/list/page")
    public GenericResponse<Page<FieldInfo>> listMyFieldInfoByPage(FieldInfoGetRequest fieldInfoGetRequest,
                                                               HttpServletRequest request) {
        UserInfo currentUser = userInfoService.getCurrentUser(request);
        long pageNum = fieldInfoGetRequest.getPaginationNum();
        long pageSize = fieldInfoGetRequest.getPaginationSize();
        QueryWrapper<FieldInfo> queryWrapper = getQueryWrapper(fieldInfoGetRequest);
        queryWrapper.eq("userId", currentUser.getId());
        Page<FieldInfo> fieldInfoPage = fieldInfoService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return ResponseUtils.success(fieldInfoPage);
    }
    
    /**
     * 获取查询包装类
     *
     * @param fieldInfoGetRequest
     * @return
     */
    private QueryWrapper<FieldInfo> getQueryWrapper(FieldInfoGetRequest fieldInfoGetRequest) {
        if (fieldInfoGetRequest == null) {
            throw new GenericException(ResponseCode.PARAMS_ERROR, "请求参数为空");
        }
        FieldInfo fieldInfoQuery = new FieldInfo();
        BeanUtils.copyProperties(fieldInfoGetRequest, fieldInfoQuery);
        String sortColumn = fieldInfoGetRequest.getSortColumn();
        String sortOrder = fieldInfoGetRequest.getSortOrder();
        String searchParam = fieldInfoGetRequest.getSearchParam();
        fieldInfoQuery.setName(null);
        fieldInfoQuery.setFieldName(null);
        fieldInfoQuery.setContent(null);
        QueryWrapper<FieldInfo> queryWrapper = new QueryWrapper<>(fieldInfoQuery);
        queryWrapper.like(StrUtil.isNotBlank(searchParam), "name", searchParam)
                .or(StrUtil.isNotBlank(searchParam))
                .like(StrUtil.isNotBlank(searchParam), "fieldName", searchParam);
        queryWrapper.orderBy(StrUtil.isNotBlank(sortColumn), sortOrder.equals(Constants.SORT_ORDER_ASC),
                sortColumn);
        return queryWrapper;
    }
    
}
