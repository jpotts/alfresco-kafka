package com.metaversant.kafka.model;

import java.util.Set;

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
public class NodePermissions {
    
    /** The permissions. */
    private Set<NodePermission> permissions;
    
    /** The is inheritance enabled. */
    private boolean isInheritanceEnabled;
}



