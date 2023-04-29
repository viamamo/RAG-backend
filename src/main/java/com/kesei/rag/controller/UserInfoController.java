package com.kesei.rag.controller;

import cn.hutool.core.util.StrUtil;
import com.kesei.rag.entity.dto.GenericResponse;
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

/**
 * @author kesei
 */
@RestController
@RequestMapping("/user_info")
@Slf4j
public class UserInfoController {
    
    @Resource
    private UserInfoService userInfoService;
    
    /**
     * 用户注册
     *
     * @param userInfoPostRequest
     * @return
     */
    @PostMapping("/register")
    public GenericResponse<Long> userRegister(@RequestBody UserInfoPostRequest userInfoPostRequest, HttpServletRequest request) {
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
        userInfoService.userLogin(userAccount, userPassword, request);
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
}
