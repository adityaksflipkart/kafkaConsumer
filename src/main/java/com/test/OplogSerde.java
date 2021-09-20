package com.test;

import org.apache.kafka.common.serialization.Serdes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OplogSerde extends Serdes.WrapperSerde<Oplog<Inventory>> {
    @Autowired
    public OplogSerde(OplogSerializer oplogSerializer, OplogDeserializer oplogDeserializer) {
        super(oplogSerializer, oplogDeserializer);
    }
}
