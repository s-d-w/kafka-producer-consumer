package com.kpc.consumer.worker;

import com.kpc.common.kafka.KafkaConsumerClient;
import com.kpc.common.kafka.serializer.MessageDeserializer;
import com.kpc.common.schema.Message;
import com.kpc.consumer.properties.KafkaConsumerWorkerProperties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Properties;

@Component
@Scope("prototype")
public class KafkaConsumerWorker implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerWorker.class);
    private String clientId;
    private KafkaConsumerClient<String, Message> kafkaConsumerClient;

    @Autowired
    private KafkaConsumerWorkerProperties kafkaConsumerWorkerProperties;

    public KafkaConsumerWorker(String clientId) {
        this.clientId = clientId;
    }

    @PostConstruct
    public void init() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConsumerWorkerProperties.getBootStrapServers());
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MessageDeserializer.class.getCanonicalName());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConsumerWorkerProperties.getConsumerGroup());
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId);
        kafkaConsumerClient = new KafkaConsumerClient<>(properties, kafkaConsumerWorkerProperties.getTopic());
    }

    @Override
    public void run() {
        try {
            LOGGER.info("Consumer id: " + clientId + " ready for consuming.");
            while (true) {
                kafkaConsumerClient.poll(100L)
                        .map(ConsumerRecord::value)
                        .forEach(m -> LOGGER.info("Consumer {} received message: {}", clientId, m.toString()));
            }
        } catch (Exception e) {
            LOGGER.error("Error occurred during consumption: {}", e);
        } finally {
            kafkaConsumerClient.close();
        }

    }

}
