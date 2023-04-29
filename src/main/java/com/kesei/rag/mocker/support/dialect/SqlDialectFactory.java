package com.kesei.rag.mocker.support.dialect;

import com.kesei.rag.exception.GenericException;
import com.kesei.rag.mocker.support.DatabaseType;
import com.kesei.rag.mocker.support.ResponseCode;
import com.kesei.rag.support.Constants;
import com.kesei.rag.support.utils.ReflectionUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author kesei
 */
@Component
@Slf4j
public class SqlDialectFactory {
    /**
     * className => 方言实例映射
     */
    private static final Map<DatabaseType, SqlDialect> DIALECT_POOL = new ConcurrentHashMap<>();
    
    private SqlDialectFactory() {
    }
    
    @PostConstruct
    public void init(){
        ArrayList<Class<?>> classes=ReflectionUtils.getResourcesByPackage(Constants.SQL_DIALECT_PACKAGE);
        for (Class<?> clazz:classes){
            SqlDialectAnnotation annotation=clazz.getAnnotation(SqlDialectAnnotation.class);
            try {
                DIALECT_POOL.put(annotation.databaseType(), (SqlDialect) clazz.getDeclaredConstructor().newInstance());
            } catch (Exception e) {
                log.error("SqlDialectFactory construct failed, className:{}",clazz.getName(), new RuntimeException(e));
            }
        }
    }
    
    /**
     * 获取方言实例
     *
     * @param className 类名
     * @return
     */
    public static SqlDialect getDialect(DatabaseType databaseType,String className) {
        SqlDialect dialect = DIALECT_POOL.get(databaseType);
        if (null == dialect) {
            synchronized (className.intern()) {
                dialect = DIALECT_POOL.computeIfAbsent(databaseType,
                        key -> {
                            try {
                                return (SqlDialect) Class.forName(className).getDeclaredConstructor().newInstance();
                            } catch (Exception e) {
                                throw new GenericException(ResponseCode.SYSTEM_ERROR);
                            }
                        });
            }
        }
        return dialect;
    }
}
