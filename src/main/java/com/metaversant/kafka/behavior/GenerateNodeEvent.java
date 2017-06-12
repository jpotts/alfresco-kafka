package com.metaversant.kafka.behavior;

import com.metaversant.kafka.model.NodeEvent;
import com.metaversant.kafka.service.MessageService;
import com.metaversant.kafka.transform.NodeRefToNodeEvent;
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
    private ContentService contentService;
    private MessageService messageService;

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
                QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateNode"),
                ContentModel.TYPE_CMOBJECT,
                this.onCreateNode);

        this.policyComponent.bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "beforeDeleteNode"),
                ContentModel.TYPE_CMOBJECT,
                this.beforeDeleteNode);

        this.policyComponent.bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onUpdateProperties"),
                ContentModel.TYPE_CMOBJECT,
                this.onUpdateProperties);

    }

    @Override
    public void onCreateNode(ChildAssociationRef childAssocRef) {
        if (logger.isDebugEnabled()) logger.debug("Inside onCreateNode");
        NodeRef nodeRef = childAssocRef.getChildRef();
        if (nodeService.exists(nodeRef)) {
            NodeEvent e = NodeRefToNodeEvent.transform(nodeService, contentService, nodeRef);
            if (logger.isDebugEnabled()) logger.debug("Back from transform");
            e.setEventType(NodeEvent.EventType.CREATE);
            messageService.publish(e);
        }
    }

    @Override
    public void beforeDeleteNode(NodeRef nodeRef) {
        if (logger.isDebugEnabled()) logger.debug("Inside onDeleteNode");
        if (nodeService.exists(nodeRef)) {
            NodeEvent e = NodeRefToNodeEvent.transform(nodeService, contentService, nodeRef);
            e.setEventType(NodeEvent.EventType.DELETE);
            messageService.publish(e);
        }
    }

    @Override
    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> beforeProps, Map<QName, Serializable> afterProps) {
        if (logger.isDebugEnabled()) logger.debug("Inside onUpdateProperties");
        if (nodeService.exists(nodeRef)) {
            NodeEvent e = NodeRefToNodeEvent.transform(nodeService, contentService, nodeRef);
            e.setEventType(NodeEvent.EventType.UPDATE);
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

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }
}
