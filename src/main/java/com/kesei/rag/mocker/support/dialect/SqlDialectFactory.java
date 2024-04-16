package com.kesei.rag.mocker.support.dialect;

import com.kesei.rag.exception.GenericException;
import com.kesei.rag.mocker.generator.DataGeneratorAnnotation;
import com.kesei.rag.mocker.support.DatabaseType;
import com.kesei.rag.mocker.support.ResponseCode;
import com.kesei.rag.support.Constants;
import com.kesei.rag.support.utils.ReflectionUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SQL方言工厂
 * SQL方言工厂通过扫描 {@link SqlDialectAnnotation} 注解挂载生成器
 * @author viamamo
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
                log.info("SqlDialectFactory mounted:{}",clazz.getName());
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
        if(Objects.isNull(databaseType)){
            throw new GenericException(ResponseCode.SYSTEM_ERROR,"数据库类型错误");
        }
        SqlDialect dialect = DIALECT_POOL.get(databaseType);
        if (Objects.isNull(dialect)) {
            if(Objects.isNull(className)){
                throw new GenericException(ResponseCode.SYSTEM_ERROR,"方言获取失败");
            }
            synchronized (className.intern()) {
                dialect = DIALECT_POOL.computeIfAbsent(databaseType,
                        key -> {
                            try {
                                return (SqlDialect) Class.forName(className).getDeclaredConstructor().newInstance();
                            } catch (Exception e) {
                                throw new GenericException(ResponseCode.SYSTEM_ERROR,"方言获取失败");
                            }
                        });
            }
        }
        return dialect;
    }
    
    public static SqlDialect getDialect(DatabaseType databaseType){
        return getDialect(databaseType, null);
    }
}
