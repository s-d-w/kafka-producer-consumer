package com.kpc.common.kafka;

import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Properties;
import java.util.stream.Stream;

public class KafkaConsumerClient<K, V> {

    private Consumer<K, V> consumer;
    private String topic;

    public KafkaConsumerClient(Properties properties, String topic) {
        this.topic = topic;
        consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Lists.newArrayList(topic));
    }

    public Stream<ConsumerRecord<K, V>> poll(Long ms) {
        ConsumerRecords<K, V> records = consumer.poll(Duration.ofMillis(ms));
        return Streams.stream(records.records(topic));
    }

    public void close() {
        consumer.close();
    }

}
