package com.kesei.rag.mocker.support.dialect;

import com.kesei.rag.mocker.support.DatabaseType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author kesei
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface SqlDialectAnnotation {
    DatabaseType databaseType();
}
