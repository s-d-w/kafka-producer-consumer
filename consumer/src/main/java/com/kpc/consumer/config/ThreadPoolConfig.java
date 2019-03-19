package com.kpc.consumer.config;

import com.kpc.consumer.properties.ThreadPoolProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPoolConfig {

    private ThreadPoolProperties threadPoolProperties;

    public ThreadPoolConfig(ThreadPoolProperties threadPoolProperties) {
        this.threadPoolProperties = threadPoolProperties;
    }

    @Bean
    public ExecutorService createConsumerWorkerExecutor() {
        return Executors.newFixedThreadPool(threadPoolProperties.getConsumerWorkerThreads());
    }
}
