package life.qbic.portal.sampletracking.old.components.projectoverview

import life.qbic.business.project.Project
import life.qbic.business.project.load.LoadProjectsOutput
import life.qbic.portal.sampletracking.old.communication.notification.NotificationService
import life.qbic.portal.sampletracking.old.resource.ResourceService

/**
 * <b>Presenter for the load projects use case</b>
 *
 * <p>Prepares data to be presented in the view.</p>
 *
 * @since 1.0.0
 */
class LoadProjectsPresenter implements LoadProjectsOutput {
    private final ResourceService<Project> projectResourceService
    private final NotificationService notificationService

    LoadProjectsPresenter(ResourceService<Project> projectResourceService, NotificationService notificationService) {
        this.projectResourceService = projectResourceService
        this.notificationService = notificationService
    }

    @Override
    void failedExecution(String reason) {
        notificationService.publishFailure(reason)
    }

    @Override
    void loadedProjects(List<Project> projects) {
        projects.each { project ->
            // we only add it if it is not already part of the list
            if (!projectResourceService.iterator().toList().contains(project)) {
                projectResourceService.addToResource(project)
            }
        }
        notificationService.publishSuccess("Successfully loaded ${projects.size()} projects.")
    }
}
