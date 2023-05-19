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
 * 反射工具类
 *
 * @author kesei
 */
@Slf4j
public class ReflectionUtils {
    public static final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    
    public static final MetadataReaderFactory factory = new CachingMetadataReaderFactory(resolver);
    
    /**
     * 根据包名扫描类
     *
     * @param packageName 包名
     * @return 类列表
     */
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
        }
        return classes;
    }
}
