package com.kesei.rag.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author kesei
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheck {
    
    /**
     * 有任何一个角色
     *
     * @return
     */
    String[] anyRole() default "";
    
    /**
     * 必须有某个角色
     *
     * @return
     */
    String mustRole() default "";
    
}