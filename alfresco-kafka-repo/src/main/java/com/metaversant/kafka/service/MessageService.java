package com.metaversant.kafka.service;

import java.util.Properties;

import javax.annotation.PostConstruct;

import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metaversant.kafka.model.NodeEvent;
import com.metaversant.kafka.transform.NodeRefToNodeEvent;
import com.metaversant.kafka.transform.NodeRefToNodePermissions;

/**
 * Created by jpotts, Metaversant on 6/9/17.
 */
public class MessageService {

    /** The LOGGER. */
    private static final Logger LOGGER = Logger.getLogger(MessageService.class);
	
    /////////////////////  Dependencies [Start] ////////////////
    /** The node transformer. */
    private NodeRefToNodeEvent nodeTransformer;
    
    /** The node permissions transformer. */
    private NodeRefToNodePermissions nodePermissionsTransformer;
    /////////////////////  Dependencies [End] ////////////////

    /////////////////////  Settings [Start] ////////////////
    /** The topic. */
    private String topic = "alfresco-node-events";
    
    /** The bootstrap servers. */
    private String bootstrapServers = "localhost:9092";

    /** The producer. */
    private KafkaProducer<String, String> producer;

    /** The mapper. */
    private ObjectMapper mapper = new ObjectMapper();    
    /////////////////////  Settings [End] ////////////////

    /**
     * Inits the.
     */
    @PostConstruct
	public void init() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("init invoked, topic: " + this.topic + " | bootstrapServers: " + this.bootstrapServers);
		}
		producer = new KafkaProducer<>(createProducerConfig());
	}

    /**
     * Ping.
     *
     * @param nodeRef the node ref
     */
	public void ping(final NodeRef nodeRef) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ping invoked for nodeRef: " + nodeRef);
		}
		final NodeEvent nodeEvent = nodeTransformer.transform(nodeRef);
		nodeEvent.setEventType(NodeEvent.EventType.PING);
		nodeEvent.setPermissions(nodePermissionsTransformer.transform(nodeRef));
		publish(nodeEvent);
	}

    /**
     * Publish.
     *
     * @param event the event
     */
	public void publish(final NodeEvent event) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("publish invoked for event: " + event);
		}
		try {
			final String message = mapper.writeValueAsString(event);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Publishing message: " + message);
			}
			if (message != null && message.length() != 0) {
				producer.send(new ProducerRecord<String, String>(topic, message));
			}
		} catch (JsonProcessingException jpe) {
			LOGGER.error("Error occurred while publishing the event", jpe);
		}
	}

    /**
     * Creates the producer config.
     *
     * @return the properties
     */
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
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Props for initialization: " + props);
		}
		return props;
	}

    /**
     * Sets the topic.
     *
     * @param topic the new topic
     */
    public void setTopic(final String topic) {
        this.topic = topic;
    }

    /**
     * Sets the bootstrap servers.
     *
     * @param bootstrapServers the new bootstrap servers
     */
    public void setBootstrapServers(final String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    /**
     * Sets the node transformer.
     *
     * @param nodeTransformer the new node transformer
     */
    public void setNodeTransformer(final NodeRefToNodeEvent nodeTransformer) {
        this.nodeTransformer = nodeTransformer;
    }

    /**
     * Sets the node permissions transformer.
     *
     * @param nodePermissionsTransformer the new node permissions transformer
     */
    public void setNodePermissionsTransformer(final NodeRefToNodePermissions nodePermissionsTransformer) {
        this.nodePermissionsTransformer = nodePermissionsTransformer;
    }
}
