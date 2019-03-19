package com.kpc.producer.service;

import com.kpc.common.kafka.KafkaProducerClient;
import com.kpc.common.kafka.serializer.MessageSerializer;
import com.kpc.common.schema.Message;
import com.kpc.producer.properties.KafkaProperties;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Properties;

@Service
public class KafkaService {

    private KafkaProducerClient<String, Message> kafkaProducerClient;
    private KafkaProperties kafkaProperties;

    public KafkaService(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @PostConstruct
    public void init() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootStrapServers());
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArraySerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, MessageSerializer.class.getCanonicalName());
        this.kafkaProducerClient = new KafkaProducerClient<>(properties);
    }

    public void addRecord(Message message) {
        kafkaProducerClient.addRecord(kafkaProperties.getTopic(), message);
    }

}
