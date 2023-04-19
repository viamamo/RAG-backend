package com.kesei.rag.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kesei.rag.entity.po.TableInfo;

/**
 * @author kesei
 */
public interface TableInfoService extends IService<TableInfo> {
    
    /**
     * 参数校验
     *
     * @param tableInfo 表信息
     * @param add 是否为创建校验
     */
    void valid(TableInfo tableInfo, boolean add);
}
