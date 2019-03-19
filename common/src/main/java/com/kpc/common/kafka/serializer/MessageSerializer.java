package com.kpc.common.kafka.serializer;

import com.kpc.common.schema.Message;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.ByteBuffer;
import java.util.Map;

public class MessageSerializer implements Serializer<Message> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) { }

    @Override
    public byte[] serialize(String topic, Message data) {
        try {
            byte[] serializedBody;
            int stringSize;
            if (data == null)
                return null;
            else {
                if (data.getBody() != null) {
                    serializedBody = data.getBody().getBytes("UTF-8");
                    stringSize = serializedBody.length;
                } else {
                    serializedBody = new byte[0];
                    stringSize = 0;
                }
            }

            ByteBuffer buffer = ByteBuffer.allocate(4 + stringSize);
            buffer.putInt(stringSize);
            buffer.put(serializedBody);
            return buffer.array();
        } catch (Exception e) {
            throw new SerializationException("Error when serializing Message to byte[] " + e);
        }
    }

    @Override
    public void close() { }
}
