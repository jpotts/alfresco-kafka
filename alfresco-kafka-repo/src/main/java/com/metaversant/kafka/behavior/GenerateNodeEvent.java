package com.metaversant.kafka.behavior;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.apache.log4j.Logger;

import com.metaversant.kafka.model.NodeEvent;
import com.metaversant.kafka.service.MessageService;
import com.metaversant.kafka.transform.NodeRefToNodeEvent;
import com.metaversant.kafka.transform.NodeRefToNodePermissions;

/**
 * Created by jpotts, Metaversant on 6/9/17.
 */
public class GenerateNodeEvent implements NodeServicePolicies.BeforeDeleteNodePolicy,
		NodeServicePolicies.OnCreateNodePolicy, NodeServicePolicies.OnUpdatePropertiesPolicy {
	
	/** The Constant LOGGER. */
    private static final Logger LOGGER = Logger.getLogger(GenerateNodeEvent.class);

    /////////////////////  Dependencies [Start] ////////////////
    /** The node service. */
    private NodeService nodeService;
    
    /** The policy component. */
    private PolicyComponent policyComponent;
    
    /** The message service. */
    private MessageService messageService;
    
    /** The node transformer. */
    private NodeRefToNodeEvent nodeTransformer;
    
    /** The node permissions transformer. */
    private NodeRefToNodePermissions nodePermissionsTransformer;
    /////////////////////  Dependencies [End] //////////////////

    /////////////////////  Behaviours [Start] //////////////////
    /** The on create node. */
    private Behaviour onCreateNode;
    
    /** The before delete node. */
    private Behaviour beforeDeleteNode;
    
    /** The on update properties. */
    private Behaviour onUpdateProperties;
    /////////////////////  Behaviours [End] //////////////////

    /**
     * Inits the.
     */
    public void init() {
    	
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Initializing GenerateNodeEvent behaviors");
		}

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

    /**
     * On create node.
     *
     * @param childAssocRef the child assoc ref
     */
    @Override
    public void onCreateNode(final ChildAssociationRef childAssocRef) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Inside onCreateNode");
		}
        final NodeRef nodeRef = childAssocRef.getChildRef();
        if (nodeService.exists(nodeRef)) {
        	final NodeEvent nodeEvent = nodeTransformer.transform(nodeRef);
            nodeEvent.setEventType(NodeEvent.EventType.CREATE);
            nodeEvent.setPermissions(nodePermissionsTransformer.transform(nodeRef));
            messageService.publish(nodeEvent);
        }
    }

    /**
     * Before delete node.
     *
     * @param nodeRef the node ref
     */
    @Override
    public void beforeDeleteNode(final NodeRef nodeRef) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Inside onDeleteNode");
		}
        if (nodeService.exists(nodeRef)) {
        	final NodeEvent nodeEvent = nodeTransformer.transform(nodeRef);
            nodeEvent.setEventType(NodeEvent.EventType.DELETE);
            nodeEvent.setPermissions(nodePermissionsTransformer.transform(nodeRef));
            messageService.publish(nodeEvent);
        }
    }

    /**
     * On update properties.
     *
     * @param nodeRef the node ref
     * @param beforeProps the before props
     * @param afterProps the after props
     */
    @Override
	public void onUpdateProperties(final NodeRef nodeRef, final Map<QName, Serializable> beforeProps,
			final Map<QName, Serializable> afterProps) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Inside onUpdateProperties");
		}
        if (nodeService.exists(nodeRef)) {
        	final NodeEvent nodeEvent = nodeTransformer.transform(nodeRef);
            nodeEvent.setEventType(NodeEvent.EventType.UPDATE);
            nodeEvent.setPermissions(nodePermissionsTransformer.transform(nodeRef));
            messageService.publish(nodeEvent);
        }
    }

    /**
     * Sets the node service.
     *
     * @param nodeService the new node service
     */
    public void setNodeService(final NodeService nodeService) {
        this.nodeService = nodeService;
    }

    /**
     * Sets the policy component.
     *
     * @param policyComponent the new policy component
     */
    public void setPolicyComponent(final PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    /**
     * Sets the message service.
     *
     * @param messageService the new message service
     */
    public void setMessageService(final MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * Sets the node transformer.
     *
     * @param nodeTransformer the new node transformer
     */
    public void setNodeTransformer(final NodeRefToNodeEvent nodeTransformer) {
        this.nodeTransformer = nodeTransformer;
    }

    /**
     * Sets the node permissions transformer.
     *
     * @param nodePermissionsTransformer the new node permissions transformer
     */
    public void setNodePermissionsTransformer(final NodeRefToNodePermissions nodePermissionsTransformer) {
        this.nodePermissionsTransformer = nodePermissionsTransformer;
    }
}
