package com.kesei.rag.mocker.support.dialect;

import com.kesei.rag.mocker.support.ResponseCode;
import com.kesei.rag.exception.GenericException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author kesei
 */
public class SqlDialectFactory {
    /**
     * className => 方言实例映射
     */
    private static final Map<String, SqlDialect> DIALECT_POOL = new ConcurrentHashMap<>();
    
    private SqlDialectFactory() {
    }
    
    /**
     * 获取方言实例
     *
     * @param className 类名
     * @return
     */
    public static SqlDialect getDialect(String className) {
        SqlDialect dialect = DIALECT_POOL.get(className);
        if (null == dialect) {
            synchronized (className.intern()) {
                dialect = DIALECT_POOL.computeIfAbsent(className,
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
