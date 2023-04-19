package com.kesei.rag.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kesei.rag.entity.po.FieldInfo;

/**
 * @author kesei
 */
public interface FieldInfoService extends IService<FieldInfo> {
    
    /**
     * 参数校验
     *
     * @param fieldInfo 字段信息
     * @param add 是否为创建校验
     */
    void valid(FieldInfo fieldInfo, boolean add);
}
