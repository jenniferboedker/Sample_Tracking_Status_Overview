package life.qbic.business.project.load

import life.qbic.business.DataSourceException
import life.qbic.business.project.Project

/**
 * <b>Load projects</b>
 *
 * <p>This use case loads all projects for a specific person.</p>
 *
 * @since 1.0.0
 */
class LoadProjects implements LoadProjectsInput {
    private final LoadProjectsDataSource loadProjectsDataSource
    private final LastChangeDateDataSource loadLastChangeDataSource
    private final LoadProjectsOutput output

    /**
     * Default constructor for this use case
     * @param dataSource the data source to be used
     * @param output the output to where results are published
     * @since 1.0.0
     */
    LoadProjects(LoadProjectsDataSource loadProjectsDataSource, LastChangeDateDataSource loadLastChangeDataSource, LoadProjectsOutput output) {
        this.loadProjectsDataSource = loadProjectsDataSource
        this.loadLastChangeDataSource = loadLastChangeDataSource
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
            List projects = loadProjectsDataSource.fetchUserProjects()
            projects.each { it.lastChanged = loadLastChangeDataSource.getLatestChange(it.code) }
            output.loadedProjects(projects)
        } catch (DataSourceException dataSourceException) {
            output.failedExecution(dataSourceException.getMessage())
        } catch (Exception e) {
            output.failedExecution("Could not load projects")
        }
    }



}
