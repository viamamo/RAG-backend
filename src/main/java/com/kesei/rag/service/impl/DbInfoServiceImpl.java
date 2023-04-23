package com.kesei.rag.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
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

/**
 * @author kesei
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
        String password=dbInfo.getPassword();
        String host=dbInfo.getHost();
        Integer port=dbInfo.getPort();
        // 创建时，所有参数必须非空
        if (add && StrUtil.hasBlank(name, dbName,dbType,username,password,host)&& ObjectUtil.isNotEmpty(port)) {
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
        DatabaseType databaseType=MockTool.getDatabaseTypeByName(dbInfo.getDbType());
        StringBuilder stringBuilder=new StringBuilder("jdbc:");
        stringBuilder.append(databaseType.getProtocol());
        stringBuilder.append(dbInfo.getHost());
        stringBuilder.append(':');
        stringBuilder.append(ObjectUtil.defaultIfNull(dbInfo.getPort(),databaseType.getDefaultPort()));
        stringBuilder.append("/");
        stringBuilder.append(dbInfo.getDbName());
        if(StrUtil.isNotBlank(dbInfo.getProperty())) {
            stringBuilder.append(dbInfo.getProperty());
        }
        return stringBuilder.toString();
    }
    
}
