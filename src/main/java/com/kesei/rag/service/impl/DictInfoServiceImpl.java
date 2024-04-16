package com.kesei.rag.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kesei.rag.entity.po.DictInfo;
import com.kesei.rag.exception.GenericException;
import com.kesei.rag.mapper.DictInfoMapper;
import com.kesei.rag.mocker.support.ResponseCode;
import com.kesei.rag.service.DictInfoService;
import com.kesei.rag.support.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author viamamo
 */
@Service
@Slf4j
public class DictInfoServiceImpl extends ServiceImpl<DictInfoMapper, DictInfo> implements DictInfoService {
    @Override
    public void valid(DictInfo dictInfo, boolean add) {
        if (dictInfo == null) {
            throw new GenericException(ResponseCode.PARAMS_ERROR);
        }
        String content = dictInfo.getContent();
        String name = dictInfo.getName();
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
            // 对 content 进行转换
            try {
                String[] words = content.split("[,，]");
                // 移除开头结尾空格
                for (int i = 0; i < words.length; i++) {
                    words[i] = words[i].trim();
                }
                // 过滤空单词
                List<String> wordList = Arrays.stream(words)
                        .filter(StrUtil::isNotBlank)
                        .collect(Collectors.toList());
                dictInfo.setContent(JSONUtil.parseArray(wordList).toJSONString(0));
            } catch (Exception e) {
                throw new GenericException(ResponseCode.PARAMS_ERROR, "内容格式错误");
            }
        }
    }
}
