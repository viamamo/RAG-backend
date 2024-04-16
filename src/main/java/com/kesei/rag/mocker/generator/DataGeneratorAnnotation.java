package com.kesei.rag.mocker.generator;

import com.kesei.rag.mocker.support.MockType;
import org.springframework.lang.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 数据生成器标记
 * @
 * @author viamamo
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface DataGeneratorAnnotation {
    
    // 模拟类型
    @NonNull
    MockType mockType();
}
