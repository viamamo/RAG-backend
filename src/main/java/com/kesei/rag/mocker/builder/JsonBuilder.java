package com.kesei.rag.mocker.builder;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * JsonBuilder
 *
 * @author kesei
 */
@Component
@Slf4j
public class JsonBuilder {
    /**
     * 构造数据 json
     *
     * @param dataList 数据列表
     * @return 生成的 json 数组字符串
     */
    public String buildJson(List<Map<String, Object>> dataList) {
        return JSONUtil.parseArray(dataList).toStringPretty();
    }
}
