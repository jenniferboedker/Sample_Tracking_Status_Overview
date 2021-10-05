package life.qbic.portal.sampletracking.components.projectoverview

import groovy.beans.Bindable
import life.qbic.business.project.Project
import life.qbic.business.project.subscribe.Subscriber
import life.qbic.business.samples.count.StatusCount
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

    List<ProjectSummary> projectOverviews =[]
    private final ResourceService<Project> projectResourceService
    private final ResourceService<StatusCount> statusCountService

    @Bindable ProjectSummary selectedProject
    @Bindable String generatedManifest
    final Subscriber subscriber

    ProjectOverviewViewModel(ResourceService<Project> projectResourceService, ResourceService<StatusCount> statusCountService, Subscriber subscriber) {
        this.projectResourceService = projectResourceService
        this.statusCountService = statusCountService
        this.subscriber = subscriber
        fetchProjectData()
        subscribeToResources()
    }

    private void fetchProjectData() {
        projectOverviews.clear()
        projectResourceService.iterator().each { Project project ->
            addProject(project)
        }
        statusCountService.iterator().each { StatusCount statusCount ->
            updateStatusCount(statusCount)
        }
    }

    private void subscribeToResources() {
        this.projectResourceService.subscribe({
            addProject(it)
        }, Topic.PROJECT_ADDED)
        this.projectResourceService.subscribe({
            removeProject(it)
        }, Topic.PROJECT_REMOVED)
        this.projectResourceService.subscribe({
            updateProject(it)
        }, Topic.PROJECT_UPDATED)

        this.statusCountService.subscribe({
            updateStatusCount(it)
        }, Topic.SAMPLE_COUNT_UPDATE)
    }

    private void updateStatusCount(StatusCount statusCount) {
        ProjectSummary projectSummary = getProjectSummary(statusCount.projectCode)
        Optional.ofNullable(projectSummary).ifPresent({ updateProjectSummary(it, statusCount) })
    }

    private void updateProjectSummary(ProjectSummary projectSummary, StatusCount statusCount) {
        projectSummary.samplesReceived = statusCount.samplesReceived
        projectSummary.samplesQcFailed = statusCount.samplesQcFail
        projectSummary.samplesLibraryPrepFinished = statusCount.libraryPrepFinished
        projectSummary.sampleDataAvailable = statusCount.dataAvailable
        projectSummary.totalSampleCount = statusCount.samplesInProject
        this.projectOverviews[this.projectOverviews.indexOf(projectSummary)] = projectSummary
    }

    private void updateProjectSummary(ProjectSummary projectSummary, Project project) {
        projectSummary.hasSubscription = project.hasSubscription
        this.projectOverviews[this.projectOverviews.indexOf(projectSummary)] = projectSummary
    }

    private void addProject(Project project) {
        ProjectSummary projectSummary = ProjectSummary.of(project)
        projectOverviews.add(projectSummary)
    }

    private void updateProject(Project project) {
        Optional<ProjectSummary> projectSummary = Optional.ofNullable(getProjectSummary(project.code))
        projectSummary.ifPresent({ updateProjectSummary(it, project) })
        if (!projectSummary.isPresent()) {
            addProject(project)
        }
    }

    private void removeProject(Project project) {
        ProjectSummary projectOverview = getProjectSummary(project.code)
        projectOverviews.remove(projectOverview)
    }

    /**
     * Returns the project summary for a given project code based on the projects listed in the overview
     * @param projectCode The project code specifies a project
     * @return The project summary for the respective code
     */
    private ProjectSummary getProjectSummary(String projectCode) {
        ProjectSummary projectOverview = projectOverviews.find { it ->
           it.code == projectCode
        }
        return projectOverview
    }

    InputStream getManifestInputStream() {
        InputStream result = new ByteArrayInputStream(generatedManifest.getBytes())
        return result
    }
}
