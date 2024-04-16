package com.kesei.rag.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kesei.rag.entity.po.DbInfo;

/**
 * @author viamamo
 */
public interface DbInfoService extends IService<DbInfo> {
    
    /**
     * 参数校验
     *
     * @param dbInfo 词库信息
     * @param add 是否为创建校验
     */
    void valid(DbInfo dbInfo, boolean add);
    
    /**
     * 获取URL
     * @param dbInfo
     * @return
     */
    String createUrl(DbInfo dbInfo);
}
