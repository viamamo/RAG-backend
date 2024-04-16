package com.kesei.rag.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kesei.rag.entity.po.DbInfo;
import com.kesei.rag.exception.GenericException;
import com.kesei.rag.mapper.DbInfoMapper;
import com.kesei.rag.mocker.support.DatabaseType;
import com.kesei.rag.mocker.support.ResponseCode;
import com.kesei.rag.mocker.support.utils.MockTool;
import com.kesei.rag.service.DbInfoService;
import com.kesei.rag.support.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Map;

/**
 * @author viamamo
 */
@Service
@Slf4j
public class DbInfoServiceImpl extends ServiceImpl<DbInfoMapper,DbInfo> implements DbInfoService {
    
    @Override
    public void valid(DbInfo dbInfo, boolean add) {
        if (dbInfo == null) {
            throw new GenericException(ResponseCode.PARAMS_ERROR);
        }
        String name = dbInfo.getName();
        String dbName= dbInfo.getDbName();
        String dbType= dbInfo.getDbType();
        String username=dbInfo.getUsername();
        String host=dbInfo.getHost();
        Integer port=dbInfo.getPort();
        // 创建时，所有参数必须非空
        if (add && StrUtil.hasBlank(name, dbName,dbType,username,host)&& ObjectUtil.isNotEmpty(port)) {
            throw new GenericException(ResponseCode.PARAMS_ERROR);
        }
        if (StrUtil.isNotBlank(name) && name.length() > Constants.MAX_NAME_LENGTH) {
            throw new GenericException(ResponseCode.PARAMS_ERROR, "名称过长");
        }
        dbInfo.setUrl(createUrl(dbInfo));
        
    }
    
    @Override
    public String createUrl(DbInfo dbInfo) {
        log.info(dbInfo.toString());
        DatabaseType databaseType=MockTool.getDatabaseTypeByStr(dbInfo.getDbType());
        StringBuilder stringBuilder=new StringBuilder("jdbc:");
        stringBuilder.append(databaseType.getProtocol());
        stringBuilder.append(dbInfo.getHost());
        stringBuilder.append(':');
        stringBuilder.append(ObjectUtil.defaultIfNull(dbInfo.getPort(),databaseType.getDefaultPort()));
        stringBuilder.append("/");
        stringBuilder.append(dbInfo.getDbName());
        if(!dbInfo.getProperty().isEmpty()){
            stringBuilder.append("?");
            JSONObject properties= JSONUtil.parseObj(dbInfo.getProperty());
            Iterator<Map.Entry<String, Object>> iterator = properties.iterator();
            while(iterator.hasNext()){
                Map.Entry<String, Object> value=iterator.next();
                stringBuilder.append(String.format("%s=%s", value.getKey().trim(),value.getValue().toString().trim()));
                if(iterator.hasNext()){
                    stringBuilder.append("&");
                }
            }
        }
        return stringBuilder.toString();
    }
    
}
