package life.qbic.business.project.load

/**
 * <b>Input interface for the {@link LoadProjects} feature</b>
 *
 * <p>Provides methods to trigger the loading of projects</p>>
 *
 * @since 1.0.0
 */
interface LoadProjectsInput {

    /**
     * This method calls the output interface with all projects found.
     * In case of failure the output interface failure method is called.
     * @since 1.0.0
     */
    void loadProjects()
}
