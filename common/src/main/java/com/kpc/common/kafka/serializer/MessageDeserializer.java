package com.kpc.common.kafka.serializer;

import com.kpc.common.schema.Message;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.nio.ByteBuffer;
import java.util.Map;

public class MessageDeserializer implements Deserializer<Message> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) { }

    @Override
    public Message deserialize(String topic, byte[] data) {
        try {
            if (data == null) {
                return null;
            }
            if (data.length < 4) {
                throw new SerializationException("Size of data received by IntegerDeserializer is shorter than expected");
            }

            ByteBuffer buffer = ByteBuffer.wrap(data);
            int nameSize = buffer.getInt();
            byte[] nameBytes = new byte[nameSize];
            buffer.get(nameBytes);
            String body = new String(nameBytes, "UTF-8");
            return new Message(body);
        } catch (Exception e) {
            throw new SerializationException("Error when deserializing to Message " + e);
        }
    }

    @Override
    public void close() { }
}
