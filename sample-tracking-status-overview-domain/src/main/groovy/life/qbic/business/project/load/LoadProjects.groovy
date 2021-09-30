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
class LoadProjects implements LoadProjectsInput{
    private final LoadProjectsDataSource dataSource
    private final SubscribedProjectsDataSource subscribedProjectsDataSource
    private final LoadProjectsOutput output

    /**
     * Default constructor for this use case
     * @param subscribedProjectsDataSource the data source for subscription handling
     * @param dataSource the data source to be used
     * @param output the output to where results are published
     * @since 1.0.0
     */
    LoadProjects(SubscribedProjectsDataSource subscribedProjectsDataSource, LoadProjectsDataSource dataSource, LoadProjectsOutput output) {
        this.subscribedProjectsDataSource = subscribedProjectsDataSource
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
            List<Project> projects = dataSource.fetchUserProjects()
            loadSubscriptionInformation(projects)
            output.loadedProjects(projects)
        } catch (DataSourceException dataSourceException) {
            output.failedExecution(dataSourceException.getMessage())
        } catch (Exception e) {
            output.failedExecution("Could not load projects")
        }
    }


    private void loadSubscriptionInformation(Iterable<Project> projects) {
        // Where do I get the user from?
        // List<String> subscribedProjectCodes = subscribedProjectsDataSource.findSubscribedProjectCodesFor()
    }
}
