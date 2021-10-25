package life.qbic.portal.sampletracking.resource.project

import life.qbic.business.project.Project
import life.qbic.portal.sampletracking.communication.Topic
import life.qbic.portal.sampletracking.resource.ResourceService

import java.util.function.Predicate
import java.util.function.UnaryOperator

/**
 * <p>This resource service holds projects.</p>
 * *
 * @since 1.0.0
 */
class ProjectResourceService extends ResourceService<Project> {


    ProjectResourceService() {
        addTopic(Topic.PROJECT_ADDED)
        addTopic(Topic.PROJECT_REMOVED)
        addTopic(Topic.PROJECT_UPDATED)
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

    @Override
    void replace(Predicate<Project> criteria, UnaryOperator<Project> operator) {
        List<Project> projectsBeingReplaced = content.stream().filter(criteria).collect()
        List<Project> replacements = projectsBeingReplaced.stream().map(operator).collect()
        replacements.forEach({
            publish(it, Topic.PROJECT_UPDATED)
        })
    }

}
