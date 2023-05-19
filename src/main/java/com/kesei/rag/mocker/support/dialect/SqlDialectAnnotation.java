package com.kesei.rag.mocker.support.dialect;

import com.kesei.rag.mocker.support.DatabaseType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * SQL方言标记
 * @author kesei
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface SqlDialectAnnotation {
    
    //数据库类型
    DatabaseType databaseType();
}
