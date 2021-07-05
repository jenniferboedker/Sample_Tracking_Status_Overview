package life.qbic.portal.sampletracking.components.projectoverview

import life.qbic.business.projectoverview.Project
import life.qbic.portal.sampletracking.communication.Subscriber
import life.qbic.portal.sampletracking.communication.Topic
import life.qbic.portal.sampletracking.resource.project.ProjectResourceService

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
    final ProjectResourceService projectResourceService

    Subscriber<Project> projectAddedSubscription
    Subscriber<Project> projectRemovedSubscription

    ProjectOverviewViewModel(ProjectResourceService projectResourceService){
        this.projectResourceService = projectResourceService
        fetchProjectData()
        subscribeToResources()
    }

    private void fetchProjectData() {
        projects.clear()
        projects.addAll(projectResourceService.iterator())
    }

    private void subscribeToResources() {
        this.projectResourceService.subscribe(projectAddedSubscription, Topic.PROJECT_ADDED)
        this.projectResourceService.subscribe(projectRemovedSubscription, Topic.PROJECT_REMOVED)
    }
}