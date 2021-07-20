package life.qbic.portal.sampletracking.components.projectoverview

import life.qbic.datamodel.dtos.projectmanagement.Project
import life.qbic.portal.sampletracking.communication.Topic
import life.qbic.portal.sampletracking.resource.ResourceService
import life.qbic.portal.sampletracking.resource.status.StatusCount

/**
 * <h1>ViewModel for the {@link ProjectOverviewView}</h1>
 *
 * <p>This model stores the values that are displayed on the {@link ProjectOverviewView}</p>
 *
 * @since 1.0.0
 *
*/
class ProjectOverviewViewModel {

    ObservableList projectOverviews = new ObservableList(new ArrayList<ProjectSummary>())
    private final ResourceService<Project> projectResourceService
    private final ResourceService<StatusCount> statusCountService

    ProjectOverviewViewModel(ResourceService<Project> projectResourceService, ResourceService<StatusCount> statusCountService){
        this.projectResourceService = projectResourceService
        this.statusCountService = statusCountService
        fetchProjectData()
        subscribeToResources()
    }

    private void fetchProjectData() {
        projectOverviews.clear()
        projectResourceService.iterator().each {Project project ->
            addProject(project)
        }
        statusCountService.iterator().each { StatusCount statusCount ->
            updateSamplesReceived(statusCount.projectCode, statusCount.count)
        }
    }

    private void subscribeToResources() {
        this.projectResourceService.subscribe({ addProject(it) }, Topic.PROJECT_ADDED)
        this.projectResourceService.subscribe({ removeProject(it) }, Topic.PROJECT_REMOVED)

        this.statusCountService.subscribe({updateSamplesReceived(it.projectCode, it.count)}, Topic.SAMPLE_RECEIVED_COUNT_UPDATE)
    }

    private void addProject(Project project) {
        ProjectSummary.Builder builder = new ProjectSummary.Builder(project)
        projectOverviews.add(builder.build())
    }

    private void removeProject(Project project) {
        ProjectSummary projectOverview = projectOverviews.collect {it as ProjectSummary}.find { it ->
            (it as ProjectSummary).code == project.projectId.projectCode.toString()
        }
        projectOverviews.remove(projectOverview)
    }

    private void updateSamplesReceived(String projectCode, int sampleCount) {
        ProjectSummary projectOverview = projectOverviews.collect {it as ProjectSummary}.find { it ->
            (it as ProjectSummary).code == projectCode
        }
        projectOverview.samplesReceived = sampleCount
    }
}
