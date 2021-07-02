package life.qbic.portal.sampletracking.resource.project

import life.qbic.datamodel.dtos.projectmanagement.Project
import life.qbic.portal.sampletracking.communication.Channel
import life.qbic.portal.sampletracking.communication.Topic
import life.qbic.portal.sampletracking.resource.ResourceService

/**
 * <p>This resource service holds projects.</p>
 * *
 * @since 1.0.0
 */
class ProjectResourceService extends ResourceService<Project> {

    ProjectResourceService() {
        channels.put(Topic.PROJECT_ADDED, new Channel())
        channels.put(Topic.PROJECT_REMOVED, new Channel())
    }

    /**
     * Adds a resource item to a resource of the service.
     *
     * @param resourceItem the resource item to add
     * @since 1.0.0
     */
    @Override
    void addToResource(Project resourceItem) {
        publish(resourceItem, Topic.PROJECT_ADDED)
    }

    /**
     * Removes a resource item from the resource of the service.
     *
     * @param resourceItem the resource item to remove
     * @since 1.0.0
     */
    @Override
    void removeFromResource(Project resourceItem) {
        publish(resourceItem, Topic.PROJECT_REMOVED)
    }
}
