package com.kesei.rag;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author kesei
 */
@SpringBootApplication
@EnableScheduling
@EnableAspectJAutoProxy
public class RagBackendApplication {
    // TODO distributor executor 动态识别反馈支持的dialect database
    // TODO 支持表结构检测插入 严格 非空 强制
    // TODO 通过marker撤销
    
    public static void main(String[] args) {
        SpringApplication.run(RagBackendApplication.class, args);
    }
    
}
