package com.kesei.rag.support;

import cn.hutool.core.bean.BeanUtil;
import com.kesei.rag.mocker.support.DatabaseType;
import com.kesei.rag.mocker.support.FakerType;
import com.kesei.rag.mocker.support.FieldType;
import com.kesei.rag.mocker.support.MockType;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 全局信息
 *
 * @author viamamo
 */

@Component
@Data
public class BasicInfo {
    public List<Map<String,Object>> databaseTypes=new ArrayList<>();
    public List<Map<String,Object>> fakerTypes=new ArrayList<>();
    public List<Map<String,Object>> fieldTypes=new ArrayList<>();
    public List<Map<String,Object>> mockTypes=new ArrayList<>();
    
    /**
     * 初始化全局信息
     */
    @PostConstruct
    public void init(){
        for(DatabaseType databaseType:DatabaseType.values()){
            Map<String,Object> map=BeanUtil.beanToMap(databaseType, false, true);
            map.put("key", databaseType.toString());
            databaseTypes.add(map);
        }
        for(FakerType fakerType:FakerType.values()){
            Map<String,Object> map=BeanUtil.beanToMap(fakerType, false, true);
            map.put("key", fakerType.toString());
            fakerTypes.add(map);
        }
        for(FieldType fieldType:FieldType.values()){
            Map<String,Object> map=BeanUtil.beanToMap(fieldType, false, true);
            map.put("key", fieldType.toString());
            fieldTypes.add(map);
        }
        for(MockType mockType:MockType.values()){
            Map<String,Object> map=BeanUtil.beanToMap(mockType, false, true);
            map.put("key", mockType.toString());
            mockTypes.add(map);
        }
    }
}
