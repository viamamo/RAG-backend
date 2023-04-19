package com.kesei.rag.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kesei.rag.entity.po.FieldInfo;
import com.kesei.rag.mocker.support.ResponseCode;
import com.kesei.rag.exception.GenericException;
import com.kesei.rag.mapper.FieldInfoMapper;
import com.kesei.rag.mocker.GeneratorFacade;
import com.kesei.rag.mocker.entity.MetaTable;
import com.kesei.rag.service.FieldInfoService;
import com.kesei.rag.support.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author kesei
 */
@Service
@Slf4j
public class FieldInfoServiceImpl extends ServiceImpl<FieldInfoMapper, FieldInfo> implements FieldInfoService {
    @Override
    public void valid(FieldInfo fieldInfo, boolean add) {
        if (fieldInfo == null) {
            throw new GenericException(ResponseCode.PARAMS_ERROR);
        }
        String content = fieldInfo.getContent();
        String name = fieldInfo.getName();
        // 创建时，所有参数必须非空
        if (add && StrUtil.hasBlank(name, content)) {
            throw new GenericException(ResponseCode.PARAMS_ERROR);
        }
        if (StrUtil.isNotBlank(name) && name.length() > Constants.MAX_NAME_LENGTH) {
            throw new GenericException(ResponseCode.PARAMS_ERROR, "名称过长");
        }
        if (StrUtil.isNotBlank(content)) {
            if (content.length() > Constants.MAX_CONTENT_LENGTH) {
                throw new GenericException(ResponseCode.PARAMS_ERROR, "内容过长");
            }
            // 校验字段内容
            try {
                MetaTable.MetaField metaField = JSONUtil.toBean(content, MetaTable.MetaField.class);
                GeneratorFacade.validField(metaField);
                // 填充 fieldName
                fieldInfo.setFieldName(metaField.getFieldName());
            } catch (Exception e) {
                throw new GenericException(ResponseCode.PARAMS_ERROR, "内容格式错误");
            }
        }
    }
}
