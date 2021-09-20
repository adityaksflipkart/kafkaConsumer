package com.test;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.internals.ProcessorContextImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

@Service
public class KafkaConsumer {
    public static final String INPUT_TOPIC = "inventory_event";
    public static final String OUTPUT_TOPIC = "wordcount-output";
    @Autowired
    private OplogSerde oplogSerde;

    private Properties getStreamsConfig(){
        final Properties props = new Properties();
        props.putIfAbsent(StreamsConfig.APPLICATION_ID_CONFIG, "streams-wordcount");
        props.putIfAbsent(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.putIfAbsent(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        props.putIfAbsent(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.putIfAbsent(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.putIfAbsent(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }

    public void run(){
        final Properties props = getStreamsConfig();
        final StreamsBuilder builder = new StreamsBuilder();
        final KStream<String, Oplog<Inventory>> source = builder.stream(INPUT_TOPIC, Consumed.with(Serdes.String(),oplogSerde));
        source
                .map((k,v)->{
                    System.out.println(k+"------>"+ v);
                    return new KeyValue<>(k.toUpperCase(), v);})
                .transform(new TransformerSupplier<String, Oplog<Inventory>, KeyValue<String , Oplog<Inventory>>>() {
                    @Override
                    public Transformer<String, Oplog<Inventory>, KeyValue<String, Oplog<Inventory>>> get() {
                       return  new Transformer<String, Oplog<Inventory>, KeyValue<String, Oplog<Inventory>>>() {
                           private ProcessorContext context;
                           @Override
                           public void init(ProcessorContext context) {
                               this.context = context;
                           }
                           @Override
                           public KeyValue<String, Oplog<Inventory>> transform(String key, Oplog<Inventory> value) {
                               ((ProcessorContextImpl) context).recordContext()
                                       .headers().add("test", "test".getBytes(StandardCharsets.UTF_8));
                               return new KeyValue<>(key, value);
                           }
                           @Override
                           public void close() {}
                       };
                    };
                })
                .to((key, value, recordContext) -> value.getFullDocument().getItem(),  Produced.with(Serdes.String(), oplogSerde));

        final KafkaStreams streams = new KafkaStreams(builder.build(), props);
        final CountDownLatch latch = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread("streams-wordcount-shutdown-hook") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });
        try {
            streams.start();
            latch.await();
        } catch (final Throwable e) {
            System.exit(1);
        }
        System.exit(0);
    }
}
