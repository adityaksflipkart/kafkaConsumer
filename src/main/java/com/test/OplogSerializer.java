package com.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OplogSerializer implements Serializer<Oplog<Inventory>> {
    @Autowired
    private ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public byte[] serialize(String s, Oplog<Inventory> inventoryOplog) {
        byte[] bytes = objectMapper.writeValueAsBytes(inventoryOplog);
        return bytes;
    }
}
