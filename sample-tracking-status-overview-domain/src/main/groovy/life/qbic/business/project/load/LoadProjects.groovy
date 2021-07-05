package life.qbic.business.project.load
/**
 * <b>Load projects</b>
 *
 * <p>This use case loads all projects for a specific person.</p>
 *
 * @since 1.0.0
 */
class LoadProjects implements LoadProjectsInput{
    private final LoadProjectsDataSource dataSource
    private final LoadProjectsOutput output

    /**
     * Default constructor for this use case
     * @param dataSource the data source to be used
     * @param output the output to where results are published
     * @since 1.0.0
     */
    LoadProjects(LoadProjectsDataSource dataSource, LoadProjectsOutput output) {
        this.dataSource = dataSource
        this.output = output
    }

    /**
     * This method calls the output interface with all projects found.
     * In case of failure the output interface failure method is called.
     * @since 1.0.0
     */
    @Override
    void loadProjects() {
        try {
            List projects = dataSource.fetchUserProjects()
            output.loadedProjects(projects)
        } catch (Exception e) {
            output.failedExecution(e.getMessage())
        }
    }



}
