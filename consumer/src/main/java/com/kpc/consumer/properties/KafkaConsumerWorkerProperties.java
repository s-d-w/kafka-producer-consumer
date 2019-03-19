package com.kpc.consumer.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "kafkaconsumerworker")
public class KafkaConsumerWorkerProperties {

    private String bootStrapServers;

    private String topic;

    private String consumerGroup;

    public String getBootStrapServers() {
        return bootStrapServers;
    }

    public void setBootStrapServers(String bootStrapServers) {
        this.bootStrapServers = bootStrapServers;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }
}
