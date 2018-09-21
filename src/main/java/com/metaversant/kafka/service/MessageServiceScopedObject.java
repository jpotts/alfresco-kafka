package com.metaversant.kafka.service;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.apache.log4j.Logger;

/**
 * Created by jpotts, Metaversant on 6/9/17.
 */
public class MessageServiceScopedObject extends BaseScopableProcessorExtension {
    static Logger logger = Logger.getLogger(MessageServiceScopedObject.class);

    // Dependencies
    private MessageService messageService;

    public void ping(ScriptNode scriptNode) {
        messageService.ping(scriptNode.getNodeRef());
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }
}
