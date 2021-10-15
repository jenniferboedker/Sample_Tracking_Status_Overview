package life.qbic.portal.sampletracking.components.projectoverview


import groovy.beans.Bindable
import groovy.util.logging.Log4j2
import life.qbic.business.project.Project
import life.qbic.business.project.subscribe.Subscriber
import life.qbic.datamodel.dtos.business.ProjectManager
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
@Log4j2
class ProjectOverviewViewModel {

    ObservableList projectOverviews = new ObservableList(new ArrayList<ProjectSummary>())
    private final ResourceService<Project> projectResourceService
    private final ResourceService<StatusCount> statusCountService

    @Bindable ProjectSummary selectedProject
    @Bindable String generatedManifest
    final Subscriber subscriber
    boolean isProjectUpdated = false

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
            updateSamplesReceived(statusCount)
            updateSamplesFailedQc(statusCount)
            updateSamplesLibraryPrepFinished(statusCount)
            updateDataAvailable(statusCount)
        }
    }

    private void subscribeToResources() {
        this.projectResourceService.subscribe({ addProject(it) }, Topic.PROJECT_ADDED)
        this.projectResourceService.subscribe({ removeProject(it) }, Topic.PROJECT_REMOVED)
        this.projectResourceService.subscribe({updateProject(it)}, Topic.PROJECT_SUBSCRIPTION_UPDATED)

        this.statusCountService.subscribe({ updateSamplesReceived(it) }, Topic.SAMPLE_RECEIVED_COUNT_UPDATE)
        this.statusCountService.subscribe({ updateSamplesFailedQc(it) }, Topic.SAMPLE_FAILED_QC_COUNT_UPDATE)
        this.statusCountService.subscribe({ updateDataAvailable(it) }, Topic.SAMPLE_DATA_AVAILABLE_COUNT_UPDATE)
        this.statusCountService.subscribe({ updateSamplesLibraryPrepFinished(it) }, Topic.SAMPLE_LIBRARY_PREP_FINISHED)
    }

    private void addProject(Project project) {
        projectOverviews.add(ProjectSummary.of(project))
    }

    private void removeProject(Project project) {
        List<ProjectSummary> summaries = projectOverviews as List<ProjectSummary>
        ProjectSummary projectOverview = summaries.find { it ->
            it.code == project.code
        }
        projectOverviews.remove(projectOverview)
    }


    private void updateProject(Project project){
        log.info "Project ${project.code} - ${project.title} was updated, grid will be reloaded."
        isProjectUpdated = true
    }

    private void updateSamplesReceived(StatusCount statusCount) {
        ProjectSummary summary = getProjectSummary(statusCount.projectCode)
        summary.samplesReceived = statusCount.count
        int totalSampleCount = statusCount.totalSampleCount
        summary.totalSampleCount = totalSampleCount
    }

    private void updateDataAvailable(StatusCount statusCount) {
        ProjectSummary summary = getProjectSummary(statusCount.projectCode)
        summary.sampleDataAvailable = statusCount.count
        int totalSampleCount = statusCount.totalSampleCount
        summary.totalSampleCount = totalSampleCount
    }

    private void updateSamplesFailedQc(StatusCount statusCount) {
        ProjectSummary summary = getProjectSummary(statusCount.projectCode)
        summary.samplesQcFailed = statusCount.count
        int totalSampleCount = statusCount.totalSampleCount
        summary.totalSampleCount = totalSampleCount
    }

    private void updateSamplesLibraryPrepFinished(StatusCount statusCount) {
        ProjectSummary summary = getProjectSummary(statusCount.projectCode)
        summary.samplesLibraryPrepFinished = statusCount.count
        int totalSampleCount = statusCount.totalSampleCount
        summary.totalSampleCount = totalSampleCount
    }

    /**
     * Returns the project summary for a given project code based on the projects listed in the overview
     * @param projectCode The project code specifies a project
     * @return The project summary for the respective code
     */
    private ProjectSummary getProjectSummary(String projectCode) {
        ProjectSummary projectOverview = projectOverviews.collect { it as ProjectSummary }.find { it ->
            (it as ProjectSummary).code == projectCode
        }
        return projectOverview
    }

    InputStream getManifestInputStream() {
        InputStream result = new ByteArrayInputStream(generatedManifest.getBytes())
        return result
    }
}
