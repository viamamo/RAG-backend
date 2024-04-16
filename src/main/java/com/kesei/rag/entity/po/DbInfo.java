package com.kesei.rag.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 数据库
 * @author viamamo
 */
@TableName(value ="db_info")
@Data
public class DbInfo implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 系统名
     */
    private String name;
    
    /**
     * 数据库名
     */
    private String dbName;
    
    /**
     * 数据库类型
     */
    private String dbType;
    
    /**
     * driver
     */
    private String driver;
    
    /**
     * 地址
     */
    private String url;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * db参数
     */
    private String property;
    
    /**
     * 主机
     */
    private String host;
    
    /**
     * 端口
     */
    private Integer port;
    
    /**
     * 创建用户 id
     */
    private Long userId;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
    
    /**
     * 是否删除
     */
    private Integer isDelete;
    
    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}