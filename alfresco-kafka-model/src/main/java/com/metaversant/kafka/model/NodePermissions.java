package com.metaversant.kafka.model;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

/**
 * Created by jpotts, Metaversant on 6/9/17.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NodePermissions {
    private Set<NodePermission> permissions;
    private boolean isInheritanceEnabled;
}



