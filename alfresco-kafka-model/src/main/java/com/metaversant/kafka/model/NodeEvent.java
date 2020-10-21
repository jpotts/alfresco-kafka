package com.metaversant.kafka.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Created by jpotts, Metaversant on 6/9/17.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NodeEvent {
    
    /**
     * The Enum EventType.
     */
    public enum EventType {
        
        /** The create. */
        CREATE,
        
        /** The update. */
        UPDATE,
        
        /** The delete. */
        DELETE,
        
        /** The ping. */
        PING,
        
        /** The grant. */
        GRANT,
        
        /** The revoke. */
        REVOKE,
        
        /** The enable inherit. */
        ENABLE_INHERIT,
        
        /** The disable inherit. */
        DISABLE_INHERIT
    }

    /** The node ref. */
    private String nodeRef;
    
    /** The event type. */
    private NodeEvent.EventType eventType;
    
    /** The path. */
    private String path;
    
    /** The created. */
    private Date created;
    
    /** The modified. */
    private Date modified;
    
    /** The creator. */
    private String creator;
    
    /** The modifier. */
    private String modifier;
    
    /** The mimetype. */
    private String mimetype;
    
    /** The content type. */
    private String contentType;
    
    /** The site id. */
    private String siteId;
    
    /** The size. */
    private Long size;
    
    /** The parent. */
    private String parent;
    
    /** The authority. */
    private String authority;
    
    /** The permission. */
    private String permission;
    
    /** The permissions. */
    private NodePermissions permissions;
    
    /** The tags. */
    private List<String> tags;
}
