package life.qbic.portal.sampletracking.components.projectoverview

import com.vaadin.ui.CheckBox
import groovy.beans.Bindable
import groovy.util.logging.Log4j2
import life.qbic.business.project.Project
import life.qbic.business.project.subscribe.Subscriber
import life.qbic.business.samples.count.StatusCount
import life.qbic.portal.sampletracking.communication.Channel
import life.qbic.portal.sampletracking.communication.Topic
import life.qbic.portal.sampletracking.components.projectoverview.LastChangedComparator.SortOrder
import life.qbic.portal.sampletracking.resource.ResourceService

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

    List<ProjectSummary> projectOverviews =[]
    private final ResourceService<Project> projectResourceService
    private final ResourceService<StatusCount> statusCountService

    @Bindable ProjectSummary selectedProject
    @Bindable String generatedManifest
    final Subscriber subscriber
    final Channel<String> updatedProjectsChannel

    ProjectOverviewViewModel(ResourceService<Project> projectResourceService, ResourceService<StatusCount> statusCountService, Subscriber subscriber) {
        this.updatedProjectsChannel = new Channel<>()
        this.projectResourceService = projectResourceService
        this.statusCountService = statusCountService
        this.subscriber = subscriber

        fetchProjectData()
        subscribeToResources()
        bindManifestToSelection()
    }

    private void fetchProjectData() {
        projectOverviews.clear()
        projectResourceService.iterator().each { Project project ->
            addProject(project)
        }
        Collections.sort(projectOverviews, new LastChangedComparator(SortOrder.DESCENDING))

        statusCountService.iterator().each { StatusCount statusCount ->
            updateStatusCount(statusCount)
        }
    }

    private void subscribeToResources() {
        this.projectResourceService.subscribe({ addProject(it) }, Topic.PROJECT_ADDED)
        this.projectResourceService.subscribe({ removeProject(it) }, Topic.PROJECT_REMOVED)
        this.projectResourceService.subscribe({ updateProject(it) }, Topic.PROJECT_UPDATED)
        this.statusCountService.subscribe({
            updateStatusCount(it) }, Topic.SAMPLE_COUNT_UPDATE)
    }

    private void updateStatusCount(StatusCount statusCount) {
        ProjectSummary projectSummary = getProjectSummary(statusCount.projectCode)
        Optional.ofNullable(projectSummary).ifPresent({ updateProjectSummary(it, statusCount) })
    }

    private void updateProjectSummary(ProjectSummary projectSummary, StatusCount statusCount) {
        projectSummary.samplesReceived.totalSampleCount = statusCount.samplesInProject
        projectSummary.samplesReceived.passingSamples = statusCount.samplesReceived

        projectSummary.samplesQc.totalSampleCount = statusCount.samplesInProject
        projectSummary.samplesQc.failingSamples = statusCount.samplesQcFail
        projectSummary.samplesQc.passingSamples = statusCount.samplesQcPass

        projectSummary.samplesLibraryPrepFinished.totalSampleCount = statusCount.samplesInProject
        projectSummary.samplesLibraryPrepFinished.passingSamples = statusCount.libraryPrepFinished

        projectSummary.sampleDataAvailable.totalSampleCount = statusCount.samplesInProject
        projectSummary.sampleDataAvailable.passingSamples = statusCount.dataAvailable

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
            log.error("Tried to update ${project?.code} - ${project?.title} but project was not found in project list.")
            return
        }
        log.info "Project ${project.code} - ${project.title} was updated, grid will be reloaded."
        updatedProjectsChannel.publish(project.code)
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
    protected ProjectSummary getProjectSummary(String projectCode) {
        List<ProjectSummary> projectOverviews = projectOverviews.findAll { it ->
           it.code == projectCode
        }
        if (projectOverviews.size() > 1) {
            log.error("More than one project summaries for project code $projectCode")
            log.error(projectOverviews)
        }
        ProjectSummary projectSummary = projectOverviews.first()
        return projectSummary
    }

    /**
     * Makes sure that the manifest is reset when a selected project is removed
     */
    private void bindManifestToSelection() {
        addPropertyChangeListener("selectedProject", {
            ProjectSummary newValue = it.newValue as ProjectSummary
            if (newValue == null) {
                setGeneratedManifest(null)
            } else {
                if (newValue.sampleDataAvailable.passingSamples < 1) {
                    setGeneratedManifest(null)
                }
            }
        })
    }

    InputStream getManifestInputStream() {
        InputStream result = new ByteArrayInputStream(generatedManifest.getBytes())
        return result
    }
}
