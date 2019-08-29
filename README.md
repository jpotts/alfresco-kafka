# Alfresco Kafka Integration

This is an Alfresco repository tier AMP that will publish events to an Apache
Kafka topic when nodes are created, updated, or deleted. Events are also
produced for permission changes, including when inheritance is enabled and
disabled.

Events are generated using an Alfresco behavior that is bound to various class
policies such as: `onCreateNode`, `onUpdateProperties`, and `beforeDeleteNode`.

## Build

This project uses the Alfresco Maven SDK to build. The output is an AMP that can
be installed into your Alfresco WAR using the Alfresco Module Management Tool
(MMT).

## Configure

There are two configurable settings:

* Topic: Set the topic that the events should be published to. (Default: alfresco-node-events)
* Bootstrap Servers: Set the Kafka servers to publish to. (Default: localhost:9092)

Both of these settings can be configured by setting the following in alfresco-global.properties:

    kafka.topic=alfresco-node-events
    kafka.server=localhost:9092

## Example

This section includes an example of the event JSON. The events are all the same format with the exception of the event
type, which can be one of:

* CREATE
* UPDATE
* DELETE
* PING
* GRANT
* REVOKE
* ENABLE_INHERIT
* DISABLE_INHERIT

Here is what an event looks like:

    {
        "nodeRef": "70c60aea-d390-4fc7-b836-210c2778035d",
        "eventType": "CREATE",
        "path": "/{http://www.alfresco.org/model/application/1.0}company_home/{http://www.alfresco.org/model/site/1.0}sites/{http://www.alfresco.org/model/content/1.0}jtp-test-site-1/{http://www.alfresco.org/model/content/1.0}documentLibrary/{http://www.alfresco.org/model/content/1.0}test2.txt",
        "created": 1567086666248,
        "modified": 1567086666248,
        "creator": "admin",
        "modifier": "admin",
        "mimetype": "text/plain",
        "contentType": "content",
        "siteId": "jtp-test-site-1",
        "size": 4,
        "parent": "244a089b-17ad-4a49-81f4-c8b17b7322a6",
        "permissions": {
            "permissions": [
                {
                    "authority": "GROUP_site_jtp-test-site-1_SiteConsumer",
                    "authorityType": "GROUP",
                    "permission": "SiteConsumer",
                    "inherited": true
                },
                {
                    "authority": "GROUP_site_jtp-test-site-1_SiteCollaborator",
                    "authorityType": "GROUP",
                    "permission": "SiteCollaborator",
                    "inherited": true
                },
                {
                    "authority": "GROUP_site_jtp-test-site-1_SiteContributor",
                    "authorityType": "GROUP",
                    "permission": "SiteContributor",
                    "inherited": true
                },
                {
                    "authority": "GROUP_site_jtp-test-site-1_SiteManager",
                    "authorityType": "GROUP",
                    "permission": "SiteManager",
                    "inherited": true
                }
            ],
            "inheritanceEnabled": true
        }
    }

For folders, the size and mimetype are null and are not included in the JSON.

    {
        "nodeRef": "8d8396a1-b9f8-444d-b9ea-f3a1e6971285",
        "eventType": "UPDATE",
        "path": "/{http://www.alfresco.org/model/application/1.0}company_home/{http://www.alfresco.org/model/site/1.0}sites/{http://www.alfresco.org/model/content/1.0}jtp-test-site-1/{http://www.alfresco.org/model/content/1.0}documentLibrary/{http://www.alfresco.org/model/content/1.0}testfolder4",
        "created": 1567086551982,
        "modified": 1567086551982,
        "creator": "admin",
        "modifier": "admin",
        "contentType": "folder",
        "siteId": "jtp-test-site-1",
        "parent": "244a089b-17ad-4a49-81f4-c8b17b7322a6",
        "permissions": {
            "permissions": [
                {
                    "authority": "GROUP_site_jtp-test-site-1_SiteConsumer",
                    "authorityType": "GROUP",
                    "permission": "SiteConsumer",
                    "inherited": true
                },
                {
                    "authority": "GROUP_site_jtp-test-site-1_SiteCollaborator",
                    "authorityType": "GROUP",
                    "permission": "SiteCollaborator",
                    "inherited": true
                },
                {
                    "authority": "GROUP_site_jtp-test-site-1_SiteContributor",
                    "authorityType": "GROUP",
                    "permission": "SiteContributor",
                    "inherited": true
                },
                {
                    "authority": "GROUP_site_jtp-test-site-1_SiteManager",
                    "authorityType": "GROUP",
                    "permission": "SiteManager",
                    "inherited": true
                }
            ],
            "inheritanceEnabled": true
        }
    }

Permissions-related events are smaller. For GRANT, REVOKE, ENABLE_INHERIT, and
DISABLE_INHERIT, the event contains only the node reference and the current
access control list:

    {
        "nodeRef": "953e16c4-d1f7-421e-bbe1-4f1123429c2a",
        "eventType": "DISABLE_INHERIT",
        "permissions": {
            "permissions": [
                {
                    "authority": "GROUP_site_jtp-test-site-1_SiteManager",
                    "authorityType": "GROUP",
                    "permission": "SiteManager",
                    "inherited": false
                },
                {
                    "authority": "tuser1",
                    "authorityType": "USER",
                    "permission": "SiteContributor",
                    "inherited": false
                },
                {
                    "authority": "tuser3",
                    "authorityType": "USER",
                    "permission": "SiteContributor",
                    "inherited": false
                }
            ],
            "inheritanceEnabled": false
        }
    }

In the example above, inheritance was turned off for a folder, which left the
object with only locally-set permissions.

## Running Locally

Assuming you are running Kafka on the same machine as this project, do this:

1. From $KAFKA_HOME, run `bin/zookeeper-server-start.sh config/zkeeper.properties`
2. From $KAFKA_HOME, run `bin/kafka-server-start.sh config/server.properties`
3. From this project's root directory, run `mvn clean install alfresco:run`
4. If you want to watch the messages as they are sent to Kafka, then from $KAFKA_HOME, run `bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic alfresco-node-events --from-beginning`

## Consuming Events

For an example showing how to consume these events from a Spring Boot app, see
[this project](https://github.com/jpotts/alfresco-kafka-listener-example) on
GitHub.
