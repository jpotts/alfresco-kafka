package com.metaversant.kafka.model;

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
public class NodePermission {
    
    /** The authority. */
    private String authority;
    
    /** The authority type. */
    private String authorityType;
    
    /** The permission. */
    private String permission;
    
    /** The is inherited. */
    private boolean isInherited;
}



