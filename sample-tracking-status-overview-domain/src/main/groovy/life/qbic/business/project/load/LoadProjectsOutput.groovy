package life.qbic.business.project.load

import life.qbic.datamodel.dtos.projectmanagement.Project

/**
 * <b>Output interface for the {@link LoadProjects} feature</b>
 *
 * <p>Publishes results from loading projects</p>
 *
 * @since 1.0.0
 */
interface LoadProjectsOutput {

    /**
     * To be called when the execution of the project loading failed
     * @param reason the reason why the project retrieval failed
     * @param identifier the identifier for which it failed
     * @since 1.0.0
     */
    void failedExecution(String reason, String identifier)

    /**
     * To be called after successfully loading projects for the provided identifier.
     * @param projects all projects that could be loaded
     * @param identifier the identifier for which the projects were loaded
     * @since 1.0.0
     */
    void loadedProjects(List<Project> projects, String identifier)
}
