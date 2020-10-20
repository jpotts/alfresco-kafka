package com.metaversant.kafka.service;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.apache.log4j.Logger;

/**
 * Created by jpotts, Metaversant on 6/9/17.
 */
public class MessageServiceScopedObject extends BaseScopableProcessorExtension {
    
    /** The LOGGER. */
    private static final Logger LOGGER = Logger.getLogger(MessageServiceScopedObject.class);

    /////////////////////  Dependencies [Start] ////////////////
    /** The message service. */
    private MessageService messageService;
    /////////////////////  Dependencies [End] ////////////////

    /**
     * Ping.
     *
     * @param scriptNode the script node
     */
    public void ping(final ScriptNode scriptNode) {
    	if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ping invoked for scriptNode: " + scriptNode);
		}
        messageService.ping(scriptNode.getNodeRef());
    }

    /**
     * Sets the message service.
     *
     * @param messageService the new message service
     */
    public void setMessageService(final MessageService messageService) {
        this.messageService = messageService;
    }
}
