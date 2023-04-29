package com.kesei.rag.support.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author kesei
 */
@Slf4j
public class ReflectionUtils {
    public static final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    
    public static final MetadataReaderFactory factory = new CachingMetadataReaderFactory(resolver);
    
    public static ArrayList<Class<?>> getResourcesByPackage(String packageName){
        String resourcePattern = "/**/*.class";
        String pattern = resolver.CLASSPATH_ALL_URL_PREFIX + ClassUtils
                .convertClassNameToResourcePath(packageName) + resourcePattern;
        Resource[] resources;
        try {
            resources = resolver.getResources(pattern);
        } catch (IOException e) {
            log.error("ReflectionUtils: get resources failed,packageName:{}",packageName);
            throw new RuntimeException(e);
        }
        ArrayList<Class<?>> classes=new ArrayList<>();
        for (Resource resource: resources) {
            try {
                MetadataReader reader = factory.getMetadataReader(resource);
                //扫描到的class
                String className = reader.getClassMetadata().getClassName();
                //获取到类名
                Class<?> clazz = Class.forName(className);
                classes.add(clazz);
            } catch (IOException e) {
                log.error("ReflectionUtils: get MetadataReader failed, resource:{}",resource.getFilename());
            } catch (ClassNotFoundException e) {
                log.error("ReflectionUtils: class not found, resource:{}",resource.getFilename());
            }
            // //判断类名上是否有  @UserLoginToken 注解
            // UserLoginToken annotation = clazzName.getAnnotation(UserLoginToken.class);
            // if (annotation != null) {
            //     System.out.println("类中的注解："+ annotation.annotationType().getName());
            // }
        }
        return classes;
    }
}
