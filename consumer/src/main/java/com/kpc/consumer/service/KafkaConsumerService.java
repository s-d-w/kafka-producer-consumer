package com.kpc.consumer.service;

import com.kpc.consumer.factory.KafkaConsumerWorkerFactory;
import com.kpc.consumer.properties.ThreadPoolProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Service
public class KafkaConsumerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerService.class);

    private ThreadPoolProperties threadPoolProperties;
    private KafkaConsumerWorkerFactory kafkaConsumerWorkerFactory;
    private ExecutorService consumerWorkerExecutor;
    private AtomicInteger clientSuffix;

    public KafkaConsumerService(ThreadPoolProperties threadPoolProperties,
                                KafkaConsumerWorkerFactory kafkaConsumerWorkerFactory,
                                ExecutorService consumerWorkerExecutor) {
        this.threadPoolProperties = threadPoolProperties;
        this.kafkaConsumerWorkerFactory = kafkaConsumerWorkerFactory;
        this.consumerWorkerExecutor = consumerWorkerExecutor;
        this.clientSuffix = new AtomicInteger();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void registerConsumers() {
        LOGGER.info("Registering consumers.. ");
        Stream.generate(this::generateClientId)
                .limit(threadPoolProperties.getConsumerWorkerThreads())
                .map(kafkaConsumerWorkerFactory::builderConsumerWorker)
                .forEach(consumerWorkerExecutor::execute);
    }

    private String generateClientId() {
        return "client" + clientSuffix.getAndIncrement();
    }

}
