package com.kesei.rag.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kesei.rag.entity.po.UserInfo;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author kesei
 */
public interface UserInfoService extends IService<UserInfo> {
    
    /**
     * 用户注册
     *
     * @param userName 用户名
     * @param userAccount 用户账户
     * @param userPassword 用户密码
     * @param checkPassword 校验密码
     * @param userRole 用户角色
     * @return 新用户 id
     */
    long userRegister(String userName, String userAccount, String userPassword, String checkPassword, String userRole);
    
    /**
     * 用户登录
     *
     * @param userAccount 用户账户
     * @param userPassword 用户密码
     * @param request request
     * @return 脱敏后的用户信息
     */
    UserInfo userLogin(String userAccount, String userPassword, HttpServletRequest request);
    
    /**
     * 获取当前登录用户
     *
     * @param request request
     * @return 当前登录用户信息
     */
    UserInfo getCurrentUser(HttpServletRequest request);
    
    /**
     * 是否为管理员
     *
     * @param request request
     * @return bool
     */
    boolean isAdmin(HttpServletRequest request);
    
    /**
     * 用户注销
     *
     * @param request request
     * @return bool
     */
    boolean userLogout(HttpServletRequest request);
}
