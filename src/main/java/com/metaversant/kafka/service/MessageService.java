package com.metaversant.kafka.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metaversant.kafka.behavior.GenerateNodeEvent;
import com.metaversant.kafka.model.NodeEvent;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import java.util.Properties;

/**
 * Created by jpotts, Metaversant on 6/9/17.
 */
public class MessageService {

    // Settings
    private String topic = "alfresco-node-events";
    private String bootstrapServers = "localhost:9092";

    private KafkaProducer<String, String> producer;

    private ObjectMapper mapper = new ObjectMapper();

    private Logger logger = Logger.getLogger(GenerateNodeEvent.class);

    @PostConstruct
    public void init() {
        producer = new KafkaProducer<>(createProducerConfig());
    }

    public void publish(NodeEvent event) {
        try {
            final String message = mapper.writeValueAsString(event);

            if (message != null && message.length() != 0) {
                producer.send(new ProducerRecord<String, String>(topic, message));
            }
        } catch (JsonProcessingException jpe) {
            logger.error(jpe);
        }
    }

    private Properties createProducerConfig() {
        final Properties props = new Properties();

        props.put("bootstrap.servers", bootstrapServers);
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        return props;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }
}
