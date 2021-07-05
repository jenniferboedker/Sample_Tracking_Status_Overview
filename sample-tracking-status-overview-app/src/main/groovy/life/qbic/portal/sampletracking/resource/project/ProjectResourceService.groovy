package life.qbic.portal.sampletracking.resource.project

import life.qbic.datamodel.dtos.projectmanagement.Project
import life.qbic.portal.sampletracking.communication.Topic
import life.qbic.portal.sampletracking.resource.ResourceService

/**
 * <p>This resource service holds projects.</p>
 * *
 * @since 1.0.0
 */
class ProjectResourceService extends ResourceService<Project> {

    ProjectResourceService() {
        addTopic(Topic.PROJECT_ADDED)
        addTopic(Topic.PROJECT_REMOVED)
    }

    /**
     * Adds a resource item to a resource of the service.
     *
     * @param resourceItem the resource item to add
     * @since 1.0.0
     */
    @Override
    void addToResource(Project resourceItem) {
        content.add(resourceItem)
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
        content.remove(resourceItem)
        publish(resourceItem, Topic.PROJECT_REMOVED)
    }
}
