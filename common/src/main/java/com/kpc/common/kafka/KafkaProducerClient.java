package com.kpc.common.kafka;

import org.apache.kafka.clients.producer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Properties;

public class KafkaProducerClient<K, V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducerClient.class);

    private Producer<K, V> producer;

    public KafkaProducerClient(Properties properties) {
        producer = new KafkaProducer<>(properties);
    }

    public void addRecord(String topic, V record) {
        ProducerRecord<K, V> producerRecord = new ProducerRecord<>(topic, record);
        producer.send(producerRecord, getCallback(topic));
    }

    public void close() {
        producer.close();
    }

    private Callback getCallback(String topic) {
        return (metadata, exception) -> {
            if (Optional.ofNullable(exception).isPresent()) {
                LOGGER.error("Failed to add record to topic: {}, reason: {}", topic, exception.getMessage());
            }
        };
    }

}
