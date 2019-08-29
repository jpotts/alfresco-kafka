package com.metaversant.kafka.transform;

import com.metaversant.kafka.model.NodeEvent;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.cmr.tagging.TaggingService;
import org.alfresco.service.namespace.QName;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.alfresco.model.ContentModel.*;

/**
 * Created by jpotts, Metaversant on 6/9/17.
 */
public class NodeRefToNodeEvent {
    private Logger logger = Logger.getLogger(NodeRefToNodeEvent.class);

    // Dependencies
    private ContentService contentService;
    private NodeService nodeService;
    private SiteService siteService;
    private TaggingService taggingService;

    public NodeEvent transform(NodeRef nodeRef) {
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

        // If this node is in a site, add the site ID to the event
        SiteInfo siteInfo = siteService.getSite(nodeRef);
        if (siteInfo != null) {
            nodeEvent.setSiteId(siteInfo.getShortName());
        }

        // Retrieve the tags from the node and add them to the event
        List<String> tags = taggingService.getTags(nodeRef);
        nodeEvent.setTags(tags);

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

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }

    public void setTaggingService(TaggingService taggingService) {
        this.taggingService = taggingService;
    }
}
