package life.qbic.portal.sampletracking.components.projectoverview

import life.qbic.business.project.load.LoadProjectsOutput
import life.qbic.datamodel.dtos.projectmanagement.Project
import life.qbic.portal.sampletracking.resource.ResourceService

/**
 * <b>Presenter for the load projects use case</b>
 *
 * <p>Prepares data to be presented in the view.</p>
 *
 * @since 1.0.0
 */
class LoadProjectsPresenter implements LoadProjectsOutput {
    private final ResourceService<Project> projectResourceService

    LoadProjectsPresenter(ResourceService<Project> projectResourceService) {
        this.projectResourceService = projectResourceService
    }

    @Override
    void failedExecution(String reason) {
        //TODO implement
    }

    @Override
    void loadedProjects(List<Project> projects) {
        for (Project project in projects) {
            projectResourceService.addToResource(project)
        }
    }
}
