package com.kesei.rag.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kesei.rag.entity.po.UserInfo;
import com.kesei.rag.exception.GenericException;
import com.kesei.rag.mapper.UserInfoMapper;
import com.kesei.rag.mocker.support.ResponseCode;
import com.kesei.rag.service.UserInfoService;
import com.kesei.rag.support.Constants;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
/**
 * @author kesei
 */
@Service
@Slf4j
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {
    @Resource
    private UserInfoMapper userMapper;
    
    
    @Override
    public long userRegister(String userName, String userAccount, String userPassword, String checkPassword, String userRole) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userName, userAccount, userPassword, checkPassword)) {
            throw new GenericException(ResponseCode.PARAMS_ERROR, "参数为空");
        }
        if (userName.length()<Constants.MIN_USERNAME_LENGTH||userName.length() > Constants.MAX_USERNAME_LENGTH) {
            throw new GenericException(ResponseCode.PARAMS_ERROR, "用户名长度不符合要求");
        }
        if (userAccount.length() < Constants.MIN_ACCOUNT_LENGTH||userAccount.length() > Constants.MAX_ACCOUNT_LENGTH) {
            throw new GenericException(ResponseCode.PARAMS_ERROR, "用户账号长度不符合要求");
        }
        if (userPassword.length() < Constants.MIN_PASSWORD_LENGTH || userPassword.length() > Constants.MAX_PASSWORD_LENGTH) {
            throw new GenericException(ResponseCode.PARAMS_ERROR, "用户密码长度不符合要求");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new GenericException(ResponseCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        synchronized (userAccount.intern()) {
            // 账户不能重复
            QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = userMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new GenericException(ResponseCode.PARAMS_ERROR, "账号重复");
            }
            // 加密
            String encryptPassword = SecureUtil.sha256((Constants.SALT + userPassword));
            // 插入数据
            UserInfo userInfo = new UserInfo();
            userInfo.setUserName(userName);
            userInfo.setUserAccount(userAccount);
            userInfo.setUserPassword(encryptPassword);
            userInfo.setUserRole(userRole);
            boolean saveResult = this.save(userInfo);
            if (!saveResult) {
                throw new GenericException(ResponseCode.SQL_OPERATION_ERROR, "注册失败，数据库错误");
            }
            return userInfo.getId();
        }
    }
    
    @Override
    public UserInfo userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new GenericException(ResponseCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < Constants.MIN_ACCOUNT_LENGTH||userAccount.length() > Constants.MAX_ACCOUNT_LENGTH) {
            throw new GenericException(ResponseCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < Constants.MIN_PASSWORD_LENGTH || userPassword.length() > Constants.MAX_PASSWORD_LENGTH) {
            throw new GenericException(ResponseCode.PARAMS_ERROR, "密码错误");
        }
        // 2. 加密
        String encryptPassword = SecureUtil.sha256((Constants.SALT + userPassword));
        // 查询用户是否存在
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        UserInfo userInfo = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (userInfo == null) {
            throw new GenericException(ResponseCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 3. 记录用户的登录态
        request.getSession().setAttribute(Constants.USER_STATE, userInfo);
        return userInfo;
    }
    
    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @Override
    public UserInfo getCurrentUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(Constants.USER_STATE);
        if(userObj instanceof UserInfo currentUser) {
            if (currentUser.getId() == null) {
                throw new GenericException(ResponseCode.UNAUTHORIZED_ERROR);
            }
            // 从数据库查询
            long userId = currentUser.getId();
            currentUser = this.getById(userId);
            if (currentUser == null) {
                throw new GenericException(ResponseCode.UNAUTHORIZED_ERROR);
            }
            return currentUser;
        }
        else{
            throw new GenericException(ResponseCode.UNAUTHORIZED_ERROR);
        }
    }
    
    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(Constants.USER_STATE);
        if(userObj instanceof UserInfo userInfo) {
            return Constants.ROLE_ADMIN.equals(userInfo.getUserRole());
        }
        return false;
    }
    
    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        if (request.getSession().getAttribute(Constants.USER_STATE) == null) {
            throw new GenericException(ResponseCode.UNAUTHORIZED_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(Constants.USER_STATE);
        return true;
    }
}
