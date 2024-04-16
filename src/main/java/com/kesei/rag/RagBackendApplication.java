package com.kesei.rag;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author viamamo
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableAspectJAutoProxy
public class RagBackendApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(RagBackendApplication.class, args);
    }
    
}
