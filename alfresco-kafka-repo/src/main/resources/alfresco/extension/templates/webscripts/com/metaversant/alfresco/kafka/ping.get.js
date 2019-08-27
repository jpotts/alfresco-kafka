function main() {
    var recurse = false;
    if (args.recurse != null && args.recurse === 'true') {
        recurse = true;
    }
    if (args.nodeRef == null || args.nodeRef.length == 0) {
        logger.log("NodeRef arg not set");
        status.code = 400;
        status.message = "NodeRef has not been provided";
        status.redirect = true;
    } else {
        logger.log("Getting current node");
        var curNode = search.findNode(args.nodeRef);
        if (curNode == null) {
            logger.log("Node not found");
            status.code = 404;
            status.message = "No node found for nodeRef: " + args.nodeRef;
            status.redirect = true;
        } else {
            messageService.ping(curNode);
            model.nodeRef = args.nodeRef;
        }
        if (recurse && curNode.hasChildren) {
            logger.log("Recursing into list of children");
            handleChildren(curNode.children);
        }
    }
}

function handleChildren(children) {
    for (var i = 0; i < children.length; i++) {
        logger.log("Pinging " + children[i].nodeRef.toString());
        messageService.ping(children[i]);
        if (children[i].hasChildren) {
            handleChildren(children[i].children);
        }
    }
}

main();
