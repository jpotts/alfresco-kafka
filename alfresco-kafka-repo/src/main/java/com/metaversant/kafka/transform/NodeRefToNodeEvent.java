package com.metaversant.kafka.transform;

import static org.alfresco.model.ContentModel.PROP_CONTENT;
import static org.alfresco.model.ContentModel.PROP_CREATED;
import static org.alfresco.model.ContentModel.PROP_CREATOR;
import static org.alfresco.model.ContentModel.PROP_MODIFIED;
import static org.alfresco.model.ContentModel.PROP_MODIFIER;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.cmr.tagging.TaggingService;
import org.alfresco.service.namespace.QName;
import org.apache.log4j.Logger;

import com.metaversant.kafka.model.NodeEvent;

/**
 * Created by jpotts, Metaversant on 6/9/17.
 */
public class NodeRefToNodeEvent {
    
    /** The LOGGER. */
    private static final Logger LOGGER = Logger.getLogger(NodeRefToNodeEvent.class);

    /////////////////////  Dependencies [Start] ////////////////
    /** The content service. */
    private ContentService contentService;
    
    /** The node service. */
    private NodeService nodeService;
    
    /** The site service. */
    private SiteService siteService;
    
    /** The tagging service. */
    private TaggingService taggingService;
    /////////////////////  Dependencies [End] ////////////////

    /**
     * Transform.
     *
     * @param nodeRef the node ref
     * @return the node event
     */
    public NodeEvent transform(final NodeRef nodeRef) {
    	if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("NodeEvent transform invoked for nodeRef: " + nodeRef);
		}
    	final Map<QName, Serializable> props = nodeService.getProperties(nodeRef);
    	final NodeEvent nodeEvent = NodeEvent.builder()
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
    	final SiteInfo siteInfo = siteService.getSite(nodeRef);
		if (siteInfo != null) {
            nodeEvent.setSiteId(siteInfo.getShortName());
        }

        // Retrieve the tags from the node and add them to the event
		final List<String> tags = taggingService.getTags(nodeRef);
        nodeEvent.setTags(tags);

        // If this is a content object, set the mimetype and size props
		if (props.get(PROP_CONTENT) != null) {
            ContentReader reader = null;
            try {
                reader = contentService.getReader(nodeRef, PROP_CONTENT);
            } catch (Exception excp) {
				LOGGER.error("Error reading content: " + excp.getMessage(), excp);
            }

			if (reader != null) {
                nodeEvent.setMimetype(reader.getMimetype());
                nodeEvent.setSize(reader.getContentData().getSize());
            }
        }
        return nodeEvent;
    }

    /**
     * Sets the content service.
     *
     * @param contentService the new content service
     */
    public void setContentService(final ContentService contentService) {
        this.contentService = contentService;
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
     * Sets the site service.
     *
     * @param siteService the new site service
     */
    public void setSiteService(final SiteService siteService) {
        this.siteService = siteService;
    }

    /**
     * Sets the tagging service.
     *
     * @param taggingService the new tagging service
     */
    public void setTaggingService(final TaggingService taggingService) {
        this.taggingService = taggingService;
    }
}
