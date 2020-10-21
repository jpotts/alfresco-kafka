package com.metaversant.kafka.transform;

import java.util.HashSet;
import java.util.Set;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AccessPermission;
import org.alfresco.service.cmr.security.PermissionService;
import org.apache.log4j.Logger;

import com.metaversant.kafka.model.NodePermission;
import com.metaversant.kafka.model.NodePermissions;

/**
 * Created by jpotts, Metaversant on 8/28/19.
 */
public class NodeRefToNodePermissions {
	
	/** The LOGGER. */
    private static final Logger LOGGER = Logger.getLogger(NodeRefToNodePermissions.class);
    
    /////////////////////  Dependencies [Start] ////////////////
    /** The permission service. */
    private PermissionService permissionService;
    /////////////////////  Dependencies [End] ////////////////

    /**
     * Transform.
     *
     * @param nodeRef the node ref
     * @return the node permissions
     */
    public NodePermissions transform(final NodeRef nodeRef) {
    	if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("NodePermissions transform invoked for nodeRef: " + nodeRef);
		}
        // determine if the node inherits its ACL from the parent
        final boolean inherits = permissionService.getInheritParentPermissions(nodeRef);
        final NodePermissions perms = new NodePermissions();
        perms.setInheritanceEnabled(inherits);

        // convert the Alfresco object into our own
        final Set<AccessPermission> permissionSet = permissionService.getAllSetPermissions(nodeRef);
        final Set<NodePermission> set = new HashSet<>();
        for (final AccessPermission perm : permissionSet) {
            final NodePermission nodePerm = NodePermission.builder()
                    .authority(perm.getAuthority())
                    .authorityType(perm.getAuthorityType().name())
                    .permission(perm.getPermission())
                    .isInherited(perm.isInherited())
                    .build();
            set.add(nodePerm);
        }
        perms.setPermissions(set);
        return perms;
    }

    /**
     * Sets the permission service.
     *
     * @param permissionService the new permission service
     */
    public void setPermissionService(final PermissionService permissionService) {
        this.permissionService = permissionService;
    }
}
