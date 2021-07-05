package life.qbic.portal.sampletracking.resource.project

import life.qbic.datamodel.dtos.projectmanagement.Project
import life.qbic.datamodel.dtos.projectmanagement.ProjectCode
import life.qbic.datamodel.dtos.projectmanagement.ProjectIdentifier
import life.qbic.datamodel.dtos.projectmanagement.ProjectSpace
import life.qbic.portal.sampletracking.communication.Topic
import life.qbic.portal.sampletracking.resource.ResourceService

/**
 * <p>This resource service holds projects.</p>
 * *
 * @since 1.0.0
 */
class ProjectResourceService extends ResourceService<Project> {

    private final List<Project> projects

    ProjectResourceService() {
        addTopic(Topic.PROJECT_ADDED)
        addTopic(Topic.PROJECT_REMOVED)
        this.projects = new ArrayList<Project>()
        generateMockData()
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

    void generateMockData(){

        Project project1 = new Project.Builder(new ProjectIdentifier(new ProjectSpace("My Awesome ProjectSpace 1"), new ProjectCode("QABCD")), "My Awesome Project1").build()
        Project project2 = new Project.Builder(new ProjectIdentifier(new ProjectSpace("My Awesome ProjectSpace 2"), new ProjectCode("QABCE")), "My Awesome Project2").build()
        Project project3 = new Project.Builder(new ProjectIdentifier(new ProjectSpace("My Awesome ProjectSpace 3"), new ProjectCode("QABCF")), "My Awesome Project3").build()
        projects.addAll(project1,project2,project3)
    }

    @Override
    Iterator<Project> iterator() {
        return new ArrayList(projects).iterator()
    }
}
