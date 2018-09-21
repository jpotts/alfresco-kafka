package com.metaversant.kafka.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Created by jpotts, Metaversant on 6/9/17.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NodeEvent {
    public enum EventType {
        CREATE,
        UPDATE,
        DELETE,
        PING
    }

    private String nodeRef;
    private EventType eventType;
    private String path;
    private Date created;
    private Date modified;
    private String creator;
    private String modifier;
    private String mimetype;
    private String contentType;
    private String siteId;
    private Long size;
    private String parent;
}



