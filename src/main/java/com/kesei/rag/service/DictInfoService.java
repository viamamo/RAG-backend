package com.kesei.rag.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kesei.rag.entity.po.DictInfo;

/**
 * @author kesei
 */
public interface DictInfoService extends IService<DictInfo> {
    
    /**
     * 参数校验
     *
     * @param dictInfo 词库信息
     * @param add 是否为创建校验
     */
    void valid(DictInfo dictInfo, boolean add);
}
