package com.metaversant.kafka.behavior;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.permissions.PermissionServicePolicies;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.log4j.Logger;

import com.metaversant.kafka.model.NodeEvent;
import com.metaversant.kafka.service.MessageService;
import com.metaversant.kafka.transform.NodeRefToNodePermissions;


/**
 * Created by jpotts, Metaversant on 8/28/19.
 */
public class GenerateNodePermissionEvent implements
        PermissionServicePolicies.OnGrantLocalPermission,
        PermissionServicePolicies.OnRevokeLocalPermission,
        PermissionServicePolicies.OnInheritPermissionsDisabled,
        PermissionServicePolicies.OnInheritPermissionsEnabled {

    // Dependencies
    private PolicyComponent policyComponent;
    private MessageService messageService;
    private NodeRefToNodePermissions nodePermissionsTransformer;

    // Behaviours
    private Behaviour onGrantLocalPermission;
    private Behaviour onRevokeLocalPermission;
    private Behaviour onInheritPermissionsEnabled;
    private Behaviour onInheritPermissionsDisabled;

    private Logger logger = Logger.getLogger(GenerateNodePermissionEvent.class);

    public void init() {
        if (logger.isDebugEnabled()) logger.debug("Initializing GenerateNodePermissionEvent behaviors");

        // Create behaviours
        this.onGrantLocalPermission = new JavaBehaviour(this, "onGrantLocalPermission", Behaviour.NotificationFrequency.EVERY_EVENT);
        this.onRevokeLocalPermission = new JavaBehaviour(this, "onRevokeLocalPermission", Behaviour.NotificationFrequency.EVERY_EVENT);
        this.onInheritPermissionsEnabled = new JavaBehaviour(this, "onInheritPermissionsEnabled", Behaviour.NotificationFrequency.EVERY_EVENT);
        this.onInheritPermissionsDisabled = new JavaBehaviour(this, "onInheritPermissionsDisabled", Behaviour.NotificationFrequency.EVERY_EVENT);

        // Bind behaviours to node policies
        this.policyComponent.bindClassBehaviour(
                PermissionServicePolicies.OnGrantLocalPermission.QNAME,
                ContentModel.TYPE_CMOBJECT,
                this.onGrantLocalPermission);

        this.policyComponent.bindClassBehaviour(
                PermissionServicePolicies.OnRevokeLocalPermission.QNAME,
                ContentModel.TYPE_CMOBJECT,
                this.onRevokeLocalPermission);

        this.policyComponent.bindClassBehaviour(
                PermissionServicePolicies.OnInheritPermissionsEnabled.QNAME,
                ContentModel.TYPE_BASE,
                this.onInheritPermissionsEnabled);

        this.policyComponent.bindClassBehaviour(
                PermissionServicePolicies.OnInheritPermissionsDisabled.QNAME,
                ContentModel.TYPE_BASE,
                this.onInheritPermissionsDisabled);

    }

    @Override
    public void onGrantLocalPermission(NodeRef nodeRef, String authority, String permission) {
        NodeEvent nodeEvent = NodeEvent.builder()
                .eventType(NodeEvent.EventType.GRANT)
                .nodeRef(nodeRef.getId())
                .authority(authority)
                .permission(permission)
                .build();
        nodeEvent.setPermissions(nodePermissionsTransformer.transform(nodeRef));
        messageService.publish(nodeEvent);
    }

    @Override
    public void onRevokeLocalPermission(NodeRef nodeRef, String authority, String permission) {
        NodeEvent nodeEvent = NodeEvent.builder()
                .eventType(NodeEvent.EventType.REVOKE)
                .nodeRef(nodeRef.getId())
                .authority(authority)
                .permission(permission)
                .build();
        nodeEvent.setPermissions(nodePermissionsTransformer.transform(nodeRef));
        messageService.publish(nodeEvent);
    }

    @Override
    public void onInheritPermissionsDisabled(NodeRef nodeRef, boolean async) {
        logger.debug("inside onInheritPermissionsDisabled");
        NodeEvent nodeEvent = NodeEvent.builder()
                .eventType(NodeEvent.EventType.DISABLE_INHERIT)
                .nodeRef(nodeRef.getId())
                .build();
        nodeEvent.setPermissions(nodePermissionsTransformer.transform(nodeRef));
        messageService.publish(nodeEvent);
    }

    @Override
    public void onInheritPermissionsEnabled(NodeRef nodeRef) {
        logger.debug("inside onInheritPermissionsEnabled");
        NodeEvent nodeEvent = NodeEvent.builder()
                .eventType(NodeEvent.EventType.ENABLE_INHERIT)
                .nodeRef(nodeRef.getId())
                .build();
        nodeEvent.setPermissions(nodePermissionsTransformer.transform(nodeRef));
        messageService.publish(nodeEvent);
    }

    public PolicyComponent getPolicyComponent() {
        return policyComponent;
    }

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    public void setNodePermissionsTransformer(NodeRefToNodePermissions nodePermissionsTransformer) {
        this.nodePermissionsTransformer = nodePermissionsTransformer;
    }
}
