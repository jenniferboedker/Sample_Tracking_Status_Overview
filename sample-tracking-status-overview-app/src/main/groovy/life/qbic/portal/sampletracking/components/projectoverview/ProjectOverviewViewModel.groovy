package life.qbic.portal.sampletracking.components.projectoverview

import life.qbic.datamodel.dtos.projectmanagement.Project
import life.qbic.portal.sampletracking.communication.Topic
import life.qbic.portal.sampletracking.resource.ResourceService

/**
 * <h1>ViewModel for the {@link ProjectOverviewView}</h1>
 *
 * <p>This model stores the values that are displayed on the {@link ProjectOverviewView}</p>
 *
 * @since 1.0.0
 *
*/
class ProjectOverviewViewModel {

    ObservableList projects = new ArrayList<Project>()
    private final ResourceService<Project> projectResourceService

    ProjectOverviewViewModel(ResourceService<Project> projectResourceService){
        this.projectResourceService = projectResourceService
        fetchProjectData()
        subscribeToResources()
    }

    private void fetchProjectData() {
        projects.clear()
        projects.addAll(projectResourceService.iterator())
    }

    private void subscribeToResources() {
        this.projectResourceService.subscribe({ projects.add(it) }, Topic.PROJECT_ADDED)
        this.projectResourceService.subscribe({ projects.remove(it) }, Topic.PROJECT_REMOVED)
    }
}