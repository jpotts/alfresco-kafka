package com.metaversant.kafka.model;

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
public class NodePermission {
    private String authority;
    private String authorityType;
    private String permission;
    private boolean isInherited;
}



