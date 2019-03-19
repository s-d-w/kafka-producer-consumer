package com.kpc.consumer.factory;

import com.kpc.consumer.worker.KafkaConsumerWorker;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumerWorkerFactory {

    private final ObjectProvider<KafkaConsumerWorker> kafkaConsumerWorkerProvider;

    public KafkaConsumerWorkerFactory(ObjectProvider<KafkaConsumerWorker> kafkaConsumerWorkerProvider) {
        this.kafkaConsumerWorkerProvider = kafkaConsumerWorkerProvider;
    }

    public KafkaConsumerWorker builderConsumerWorker(String clientId) {
        return kafkaConsumerWorkerProvider.getObject(clientId);
    }

}
