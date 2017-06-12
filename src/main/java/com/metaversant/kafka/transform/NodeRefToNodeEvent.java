package com.metaversant.kafka.transform;

import com.metaversant.kafka.model.NodeEvent;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import static org.alfresco.model.ContentModel.*;

/**
 * Created by jpotts, Metaversant on 6/9/17.
 */
public class NodeRefToNodeEvent {
    private static Logger logger = Logger.getLogger(NodeRefToNodeEvent.class);

    public static NodeEvent transform(NodeService nodeService, ContentService contentService, NodeRef nodeRef) {
        Map<QName, Serializable> props = nodeService.getProperties(nodeRef);
        NodeEvent nodeEvent = NodeEvent.builder()
                .nodeRef(nodeRef.getId())
                .creator((String) props.get(PROP_CREATOR))
                .created((Date) props.get(PROP_CREATED))
                .modifier((String) props.get(PROP_MODIFIER))
                .modified((Date) props.get(PROP_MODIFIED))
                .path(nodeService.getPath(nodeRef).toString())
                .parent(nodeService.getPrimaryParent(nodeRef).getParentRef().getId())
                .contentType(nodeService.getType(nodeRef).toPrefixString())
                .build();

        // If this is a content object, set the mimetype and size props
        if (props.get(PROP_CONTENT) != null) {
            ContentReader reader = null;
            try {
                reader = contentService.getReader(nodeRef, PROP_CONTENT);
            } catch (Exception e) {
                logger.error("Error reading content: " + e.getMessage());
            }

            if (reader != null) {
                nodeEvent.setMimetype(reader.getMimetype());
                nodeEvent.setSize(reader.getContentData().getSize());
            }
        }

        return nodeEvent;
    }
}
