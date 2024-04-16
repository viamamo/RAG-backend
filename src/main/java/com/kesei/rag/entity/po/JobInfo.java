package com.kesei.rag.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * 作业
 * @author viamamo
 */
@TableName(value ="job_info")
@Data
public class JobInfo implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * dbInfoId
     */
    private Long dbId;
    
    /**
     * 已完成条数
     */
    private Integer finishedNum;
    
    /**
     * 模拟条数
     */
    private Integer mockNum;
    
    /**
     * 状态
     */
    private Integer status;
    
    /**
     * 内容
     */
    private String content;
    
    /**
     * 表名
     */
    private String tableName;
    
    /**
     * 数据库名
     */
    private String dbName;
    
    /**
     * 数据库类型
     */
    private String dbType;
    
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
     * 失败原因
     */
    private String exception;
    
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