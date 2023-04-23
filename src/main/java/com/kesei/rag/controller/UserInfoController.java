package com.kesei.rag.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.kesei.rag.aop.AuthCheck;
import com.kesei.rag.entity.dto.GenericResponse;
import com.kesei.rag.entity.dto.user.UserInfoGetRequest;
import com.kesei.rag.entity.dto.user.UserInfoPostRequest;
import com.kesei.rag.entity.po.UserInfo;
import com.kesei.rag.entity.vo.UserVO;
import com.kesei.rag.exception.GenericException;
import com.kesei.rag.mocker.support.ResponseCode;
import com.kesei.rag.service.UserInfoService;
import com.kesei.rag.support.Constants;
import com.kesei.rag.support.utils.ResponseUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author kesei
 */
@RestController
@RequestMapping("/user_info")
@Slf4j
public class UserInfoController {
    
    @Resource
    private UserInfoService userInfoService;
    
    // region 登录相关
    
    /**
     * 用户注册
     *
     * @param userInfoPostRequest
     * @return
     */
    @PostMapping("/register")
    public GenericResponse<Long> userRegister(@RequestBody UserInfoPostRequest userInfoPostRequest) {
        if (userInfoPostRequest == null) {
            throw new GenericException(ResponseCode.PARAMS_ERROR);
        }
        String userName = userInfoPostRequest.getUserName();
        String userAccount = userInfoPostRequest.getUserAccount();
        String userPassword = userInfoPostRequest.getUserPassword();
        String checkPassword = userInfoPostRequest.getCheckPassword();
        if (StrUtil.hasBlank(userAccount, userPassword, checkPassword)) {
            return null;
        }
        long result = userInfoService.userRegister(userName, userAccount, userPassword, checkPassword, Constants.ROLE_USER);
        return ResponseUtils.success(result);
    }
    
    /**
     * 用户登录
     *
     * @param userInfoPostRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public GenericResponse<UserInfo> userLogin(@RequestBody UserInfoPostRequest userInfoPostRequest, HttpServletRequest request) {
        if (userInfoPostRequest == null) {
            throw new GenericException(ResponseCode.PARAMS_ERROR);
        }
        String userAccount = userInfoPostRequest.getUserAccount();
        String userPassword = userInfoPostRequest.getUserPassword();
        if (StrUtil.hasBlank(userAccount, userPassword)) {
            throw new GenericException(ResponseCode.PARAMS_ERROR);
        }
        UserInfo userInfo = userInfoService.userLogin(userAccount, userPassword, request);
        return ResponseUtils.success(userInfo);
    }
    
    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public GenericResponse<Boolean> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new GenericException(ResponseCode.PARAMS_ERROR);
        }
        boolean result = userInfoService.userLogout(request);
        return ResponseUtils.success(result);
    }
    
    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @GetMapping("/get/login")
    public GenericResponse<UserVO> getCurrentUser(HttpServletRequest request) {
        UserInfo userInfo = userInfoService.getCurrentUser(request);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userInfo, userVO);
        return ResponseUtils.success(userVO);
    }
    
    // endregion
    
    // region 增删改查
    
    /**
     * 创建用户
     *
     * @param userInfoPostRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = Constants.ROLE_ADMIN)
    public GenericResponse<Long> addUserInfo(@RequestBody UserInfoPostRequest userInfoPostRequest, HttpServletRequest request) {
        if (userInfoPostRequest == null) {
            throw new GenericException(ResponseCode.PARAMS_ERROR);
        }
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(userInfoPostRequest, userInfo);
        boolean result = userInfoService.save(userInfo);
        if (!result) {
            throw new GenericException(ResponseCode.SQL_OPERATION_ERROR);
        }
        return ResponseUtils.success(userInfo.getId());
    }
    
    /**
     * 删除用户
     *
     * @param userInfoPostRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = Constants.ROLE_ADMIN)
    public GenericResponse<Boolean> deleteUserInfo(@RequestBody UserInfoPostRequest userInfoPostRequest, HttpServletRequest request) {
        if (userInfoPostRequest == null || userInfoPostRequest.getId() <= 0) {
            throw new GenericException(ResponseCode.PARAMS_ERROR);
        }
        boolean b = userInfoService.removeById(userInfoPostRequest.getId());
        return ResponseUtils.success(b);
    }
    
    /**
     * 更新用户
     *
     * @param userInfoPostRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = Constants.ROLE_ADMIN)
    public GenericResponse<Boolean> updateUserInfo(@RequestBody UserInfoPostRequest userInfoPostRequest,
                                            HttpServletRequest request) {
        if (userInfoPostRequest == null || userInfoPostRequest.getId() == null) {
            throw new GenericException(ResponseCode.PARAMS_ERROR);
        }
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(userInfoPostRequest, userInfo);
        boolean result = userInfoService.updateById(userInfo);
        return ResponseUtils.success(result);
    }
    
    /**
     * 根据 id 获取用户
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = Constants.ROLE_ADMIN)
    public GenericResponse<UserVO> getUserInfoById(int id, HttpServletRequest request) {
        if (id <= 0) {
            throw new GenericException(ResponseCode.PARAMS_ERROR);
        }
        UserInfo userInfo = userInfoService.getById(id);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userInfo, userVO);
        return ResponseUtils.success(userVO);
    }
    
    /**
     * 获取用户列表
     *
     * @param userInfoGetRequest
     * @param request
     * @return
     */
    @GetMapping("/list")
    @AuthCheck(mustRole = Constants.ROLE_ADMIN)
    public GenericResponse<List<UserVO>> listUserInfo(UserInfoGetRequest userInfoGetRequest, HttpServletRequest request) {
        UserInfo userInfoQuery = new UserInfo();
        if (userInfoGetRequest != null) {
            BeanUtils.copyProperties(userInfoGetRequest, userInfoQuery);
        }
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>(userInfoQuery);
        List<UserInfo> userInfoList = userInfoService.list(queryWrapper);
        List<UserVO> userVOList = userInfoList.stream().map(userInfo -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(userInfo, userVO);
            return userVO;
        }).collect(Collectors.toList());
        return ResponseUtils.success(userVOList);
    }
    
    /**
     * 分页获取用户列表
     *
     * @param userInfoGetRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page")
    @AuthCheck(mustRole = Constants.ROLE_ADMIN)
    public GenericResponse<Page<UserVO>> listUserInfoByPage(UserInfoGetRequest userInfoGetRequest, HttpServletRequest request) {
        UserInfo userInfoQuery = new UserInfo();
        long pageNum=Constants.DEFAULT_PAGE_NUM;
        long pageSize=Constants.DEFAULT_PAGE_SIZE;
        if (userInfoGetRequest != null) {
            BeanUtils.copyProperties(userInfoGetRequest, userInfoQuery);
            pageNum = userInfoGetRequest.getPaginationNum();
            pageSize = userInfoGetRequest.getPaginationSize();
        }
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>(userInfoQuery);
        Page<UserInfo> userInfoPage = userInfoService.page(new Page<>(pageNum, pageSize), queryWrapper);
        Page<UserVO> userVoPage = new PageDTO<>(userInfoPage.getCurrent(), userInfoPage.getSize(), userInfoPage.getTotal());
        List<UserVO> userVoList = userInfoPage.getRecords().stream().map(userInfo -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(userInfo, userVO);
            return userVO;
        }).collect(Collectors.toList());
        userVoPage.setRecords(userVoList);
        return ResponseUtils.success(userVoPage);
    }
}
