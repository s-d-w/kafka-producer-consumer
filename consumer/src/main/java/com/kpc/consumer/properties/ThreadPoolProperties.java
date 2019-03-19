package com.kpc.consumer.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "threadpool")
public class ThreadPoolProperties {

    private int consumerWorkerThreads;

    public int getConsumerWorkerThreads() {
        return consumerWorkerThreads;
    }

    public void setConsumerWorkerThreads(int consumerWorkerThreads) {
        this.consumerWorkerThreads = consumerWorkerThreads;
    }

}
