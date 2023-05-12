package com.kesei.rag.mocker.generator;

import com.kesei.rag.mocker.support.MockType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author kesei
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface DataGeneratorAnnotation {
    MockType mockType();
}
