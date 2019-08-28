package com.metaversant.kafka.behavior;

import com.metaversant.kafka.model.NodeEvent;
import com.metaversant.kafka.service.MessageService;
import com.metaversant.kafka.transform.NodeRefToNodeEvent;
import com.metaversant.kafka.transform.NodeRefToNodePermissions;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by jpotts, Metaversant on 6/9/17.
 */
public class GenerateNodeEvent implements
        NodeServicePolicies.BeforeDeleteNodePolicy,
        NodeServicePolicies.OnCreateNodePolicy,
        NodeServicePolicies.OnUpdatePropertiesPolicy {

    // Dependencies
    private NodeService nodeService;
    private PolicyComponent policyComponent;
    private MessageService messageService;
    private NodeRefToNodeEvent nodeTransformer;
    private NodeRefToNodePermissions nodePermissionsTransformer;

    // Behaviours
    private Behaviour onCreateNode;
    private Behaviour beforeDeleteNode;
    private Behaviour onUpdateProperties;

    private Logger logger = Logger.getLogger(GenerateNodeEvent.class);

    public void init() {
        if (logger.isDebugEnabled()) logger.debug("Initializing GenerateNodeEvent behaviors");

        // Create behaviours
        this.onCreateNode = new JavaBehaviour(this, "onCreateNode", Behaviour.NotificationFrequency.TRANSACTION_COMMIT);
        this.beforeDeleteNode = new JavaBehaviour(this, "beforeDeleteNode", Behaviour.NotificationFrequency.FIRST_EVENT);
        this.onUpdateProperties = new JavaBehaviour(this, "onUpdateProperties", Behaviour.NotificationFrequency.TRANSACTION_COMMIT);

        // Bind behaviours to node policies
        this.policyComponent.bindClassBehaviour(
                NodeServicePolicies.OnCreateNodePolicy.QNAME,
                ContentModel.TYPE_CMOBJECT,
                this.onCreateNode);

        this.policyComponent.bindClassBehaviour(
                NodeServicePolicies.BeforeDeleteNodePolicy.QNAME,
                ContentModel.TYPE_CMOBJECT,
                this.beforeDeleteNode);

        this.policyComponent.bindClassBehaviour(
                NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
                ContentModel.TYPE_CMOBJECT,
                this.onUpdateProperties);

    }

    @Override
    public void onCreateNode(ChildAssociationRef childAssocRef) {
        if (logger.isDebugEnabled()) logger.debug("Inside onCreateNode");
        NodeRef nodeRef = childAssocRef.getChildRef();
        if (nodeService.exists(nodeRef)) {
            NodeEvent e = nodeTransformer.transform(nodeRef);
            e.setEventType(NodeEvent.EventType.CREATE);
            e.setPermissions(nodePermissionsTransformer.transform(nodeRef));
            messageService.publish(e);
        }
    }

    @Override
    public void beforeDeleteNode(NodeRef nodeRef) {
        if (logger.isDebugEnabled()) logger.debug("Inside onDeleteNode");
        if (nodeService.exists(nodeRef)) {
            NodeEvent e = nodeTransformer.transform(nodeRef);
            e.setEventType(NodeEvent.EventType.DELETE);
            e.setPermissions(nodePermissionsTransformer.transform(nodeRef));
            messageService.publish(e);
        }
    }

    @Override
    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> beforeProps, Map<QName, Serializable> afterProps) {
        if (logger.isDebugEnabled()) logger.debug("Inside onUpdateProperties");
        if (nodeService.exists(nodeRef)) {
            NodeEvent e = nodeTransformer.transform(nodeRef);
            e.setEventType(NodeEvent.EventType.UPDATE);
            e.setPermissions(nodePermissionsTransformer.transform(nodeRef));
            messageService.publish(e);
        }
    }

    public NodeService getNodeService() {
        return nodeService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
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

    public void setNodeTransformer(NodeRefToNodeEvent nodeTransformer) {
        this.nodeTransformer = nodeTransformer;
    }

    public void setNodePermissionsTransformer(NodeRefToNodePermissions nodePermissionsTransformer) {
        this.nodePermissionsTransformer = nodePermissionsTransformer;
    }
}
