package com.metaversant.kafka.transform;

import com.metaversant.kafka.model.NodePermission;
import com.metaversant.kafka.model.NodePermissions;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AccessPermission;
import org.alfresco.service.cmr.security.PermissionService;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by jpotts, Metaversant on 8/28/19.
 */
public class NodeRefToNodePermissions {
    // Dependencies
    private NodeService nodeService;
    private PermissionService permissionService;

    public NodePermissions transform(NodeRef nodeRef) {
        // determine if the node inherits its ACL from the parent
        boolean inherits = permissionService.getInheritParentPermissions(nodeRef);
        NodePermissions perms = new NodePermissions();
        perms.setInheritanceEnabled(inherits);

        // convert the Alfresco object into our own
        Set<AccessPermission> permissionSet = permissionService.getAllSetPermissions(nodeRef);
        Set<NodePermission> set = new HashSet<>();
        for (AccessPermission perm : permissionSet) {
            NodePermission nodePerm = NodePermission.builder()
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

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }
}
