package life.qbic.portal.sampletracking.resource.project

import life.qbic.business.project.Project
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
        addTopic(Topic.PROJECT_SUBSCRIPTION_UPDATED)
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

    /**
     * Triggers the update of the project (based on its code) subscription status in the service content list
     *
     * @param projectCode The project that got updated
     * @param hasSubscription Boolean describing if the project was subscribed to
     */
    void updateResource(String projectCode, boolean hasSubscription){
        List<Project> foundProjects = content.stream().filter({Project project -> project.code == projectCode}).collect().toList()
        Project project = foundProjects.first()
        removeFromResource(project)
        println(content.size())
        project.hasSubscription = hasSubscription
        addToResource(project)
        println(content.size())


        publish(project, Topic.PROJECT_SUBSCRIPTION_UPDATED)
    }

}
