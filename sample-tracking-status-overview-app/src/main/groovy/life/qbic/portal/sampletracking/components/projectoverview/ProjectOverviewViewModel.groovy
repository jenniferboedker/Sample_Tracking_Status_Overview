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

    ObservableList projectOverviews = new ObservableList(new ArrayList<ProjectOverview>())
    private final ResourceService<Project> projectResourceService

    ProjectOverviewViewModel(ResourceService<Project> projectResourceService){
        this.projectResourceService = projectResourceService
        fetchProjectData()
        subscribeToResources()
    }

    private void fetchProjectData() {
        projectOverviews.clear()
        for (Project project : projectResourceService.iterator()) {
            addProject(project)
        }
    }

    private void subscribeToResources() {
        this.projectResourceService.subscribe({
            addProject(it)
        }, Topic.PROJECT_ADDED)
        this.projectResourceService.subscribe({
            removeProject(it)
        }, Topic.PROJECT_REMOVED)
        //TODO subscribe to changes in sample counts
    }

    private void addProject(Project project) {
        ProjectOverview.Builder builder = new ProjectOverview.Builder(project)
        projectOverviews.add(builder.build())
    }

    private void removeProject(Project project) {
        ProjectOverview projectOverview = projectOverviews.collect {it as ProjectOverview}.find { it ->
            (it as ProjectOverview).code == project.projectId.projectCode.toString()
        }
        projectOverviews.remove(projectOverview)
    }

    private void updateSamplesReceived(String projectCode, int sampleCount) {
        ProjectOverview projectOverview = projectOverviews.collect {it as ProjectOverview}.find { it ->
            (it as ProjectOverview).code == projectCode
        }
        projectOverview.samplesReceived = sampleCount
    }
}
