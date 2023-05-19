package com.kesei.rag.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kesei.rag.aop.AuthCheck;
import com.kesei.rag.entity.dto.GenericResponse;
import com.kesei.rag.entity.dto.dict.DictInfoGetRequest;
import com.kesei.rag.entity.dto.dict.DictInfoPostRequest;
import com.kesei.rag.entity.po.DictInfo;
import com.kesei.rag.entity.po.UserInfo;
import com.kesei.rag.exception.GenericException;
import com.kesei.rag.mocker.support.ResponseCode;
import com.kesei.rag.service.DictInfoService;
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
@RequestMapping("/dict_info")
@Slf4j
public class DictInfoController {
    @Resource
    private DictInfoService dictInfoService;
    @Resource
    private UserInfoService userInfoService;
    
    /**
     * 添加
     *
     * @param dictInfoPostRequest post封装
     * @return dictInfoID
     */
    @PostMapping("/add")
    public GenericResponse<Long> addDictInfo(@RequestBody DictInfoPostRequest dictInfoPostRequest, HttpServletRequest request) {
        if (dictInfoPostRequest == null) {
            throw new GenericException(ResponseCode.PARAMS_ERROR);
        }
        DictInfo dictInfo = new DictInfo();
        BeanUtils.copyProperties(dictInfoPostRequest, dictInfo);
        // 校验
        dictInfoService.valid(dictInfo, true);
        UserInfo currentUser = userInfoService.getCurrentUser(request);
        dictInfo.setUserId(currentUser.getId());
        boolean result = dictInfoService.save(dictInfo);
        if (!result) {
            throw new GenericException(ResponseCode.SQL_OPERATION_ERROR);
        }
        return ResponseUtils.success(dictInfo.getId());
    }
    
    /**
     * 删除
     *
     * @param dictInfoPostRequest post封装
     * @return 删除是否成功
     */
    @PostMapping("/delete")
    public GenericResponse<Boolean> deleteDictInfo(@RequestBody DictInfoPostRequest dictInfoPostRequest, HttpServletRequest request) {
        if (dictInfoPostRequest == null || dictInfoPostRequest.getId() <= 0) {
            throw new GenericException(ResponseCode.PARAMS_ERROR);
        }
        UserInfo currentUser = userInfoService.getCurrentUser(request);
        long id = dictInfoPostRequest.getId();
        // 判断是否存在
        DictInfo oldDictInfo = dictInfoService.getById(id);
        if (oldDictInfo == null) {
            throw new GenericException(ResponseCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!oldDictInfo.getUserId().equals(currentUser.getId()) && !userInfoService.isAdmin(request)) {
            throw new GenericException(ResponseCode.UNAUTHORIZED_ERROR);
        }
        boolean b = dictInfoService.removeById(id);
        return ResponseUtils.success(b);
    }
    
    /**
     * 根据 id 获取
     *
     * @param id dictInfoId
     * @return dictInfo
     */
    @GetMapping("/get")
    public GenericResponse<DictInfo> getDictInfoById(long id) {
        if (id <= 0) {
            throw new GenericException(ResponseCode.PARAMS_ERROR);
        }
        DictInfo dict = dictInfoService.getById(id);
        return ResponseUtils.success(dict);
    }
    
    /**
     * 获取列表
     *
     * @param dictInfoGetRequest get封装
     * @return dictInfo列表
     */
    @GetMapping("/list")
    public GenericResponse<List<DictInfo>> listDictInfo(DictInfoGetRequest dictInfoGetRequest) {
        List<DictInfo> dictInfoList = dictInfoService.list(getQueryWrapper(dictInfoGetRequest));
        return ResponseUtils.success(dictInfoList);
    }
    
    /**
     * 分页获取列表
     *
     * @param dictInfoGetRequest get封装
     * @return 分页
     */
    @GetMapping("/list/page")
    public GenericResponse<Page<DictInfo>> listDictInfoByPage(DictInfoGetRequest dictInfoGetRequest) {
        long pageNum = dictInfoGetRequest.getPaginationNum();
        long pageSize = dictInfoGetRequest.getPaginationSize();
        Page<DictInfo> dictPage = dictInfoService.page(new Page<>(pageNum, pageSize),
                getQueryWrapper(dictInfoGetRequest));
        return ResponseUtils.success(dictPage);
    }
    
    /**
     * 获取当前用户可选的全部资源列表（只返回 id 和名称）
     *
     * @param dictInfoGetRequest get封装
     * @return dictInfo列表
     */
    @GetMapping("/my/list")
    public GenericResponse<List<DictInfo>> listMyDictInfo(DictInfoGetRequest dictInfoGetRequest,
                                               HttpServletRequest request) {
        DictInfo dictInfoQuery = new DictInfo();
        if (dictInfoGetRequest == null) {
            throw new GenericException(ResponseCode.PARAMS_ERROR);
        }
        BeanUtils.copyProperties(dictInfoGetRequest, dictInfoQuery);
        QueryWrapper<DictInfo> queryWrapper = getQueryWrapper(dictInfoGetRequest);
        final String[] fields = new String[]{"id", "name"};
        queryWrapper.select(fields);
        List<DictInfo> dictList = dictInfoService.list(queryWrapper);
        try {
            UserInfo currentUser = userInfoService.getCurrentUser(request);
            dictInfoQuery.setUserId(currentUser.getId());
            queryWrapper = new QueryWrapper<>(dictInfoQuery);
            queryWrapper.select(fields);
            dictList.addAll(dictInfoService.list(queryWrapper));
        } catch (Exception e) {
            // 未登录
        }
        // 根据 id 去重
        List<DictInfo> resultList = dictList.stream().collect(Collectors.collectingAndThen(
                Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(DictInfo::getId))), ArrayList::new));
        return ResponseUtils.success(resultList);
    }
    
    /**
     * 分页获取当前用户可选的资源列表
     *
     * @param dictInfoGetRequest get封装
     * @return 分页
     */
    @GetMapping("/my/list/page")
    public GenericResponse<Page<DictInfo>> listMyDictInfoByPage(DictInfoGetRequest dictInfoGetRequest,
                                                     HttpServletRequest request) {
        UserInfo currentUser = userInfoService.getCurrentUser(request);
        long pageNum = dictInfoGetRequest.getPaginationNum();
        long pageSize = dictInfoGetRequest.getPaginationSize();
        QueryWrapper<DictInfo> queryWrapper = getQueryWrapper(dictInfoGetRequest);
        queryWrapper.eq("userId", currentUser.getId());
        Page<DictInfo> dictInfoPage = dictInfoService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return ResponseUtils.success(dictInfoPage);
    }
    
    /**
     * 获取查询包装类
     *
     * @param dictInfoGetRequest get封装
     * @return mb+查询包装
     */
    private QueryWrapper<DictInfo> getQueryWrapper(DictInfoGetRequest dictInfoGetRequest) {
        if (dictInfoGetRequest == null) {
            throw new GenericException(ResponseCode.PARAMS_ERROR, "请求参数为空");
        }
        DictInfo dictInfoQuery = new DictInfo();
        BeanUtils.copyProperties(dictInfoGetRequest, dictInfoQuery);
        String sortColumn = dictInfoGetRequest.getSortColumn();
        String sortOrder = dictInfoGetRequest.getSortOrder();
        String searchParam=dictInfoGetRequest.getSearchParam();
        dictInfoQuery.setName(null);
        dictInfoQuery.setContent(null);
        QueryWrapper<DictInfo> queryWrapper = new QueryWrapper<>(dictInfoQuery);
        queryWrapper.like(StrUtil.isNotBlank(searchParam), "name", searchParam);
        queryWrapper.orderBy(StrUtil.isNotBlank(sortColumn), sortOrder.equals(Constants.SORT_ORDER_ASC),
                sortColumn);
        return queryWrapper;
    }
}
