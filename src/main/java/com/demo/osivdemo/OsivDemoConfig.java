package com.demo.osivdemo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class OsivDemoConfig {

    public static final String DEMO_EXECUTOR = "DEMO_EXECUTOR";

    @Bean(name = DEMO_EXECUTOR)
    public ThreadPoolTaskExecutor demoTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setThreadNamePrefix("async-session-");
        executor.initialize();
        return executor;
    }
}
