package com.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OplogDeserializer implements Deserializer<Oplog<Inventory>> {
    private static String PAYLOAD_KEY="payload";
    @Autowired
    private ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public Oplog<Inventory> deserialize(String s, byte[] bytes) {
        if(bytes == null) {
            return null;
        }
        Map<String, Object> receivedOplog = objectMapper.readValue(bytes, new TypeReference<Map<String, Object>>() {});
        Object payLoad = receivedOplog.get(PAYLOAD_KEY);
        Oplog<Inventory> inventoryOplog = objectMapper.readValue((String) payLoad, new TypeReference<Oplog<Inventory>>() {});
        return inventoryOplog;
    }
}
