package life.qbic.business.project.load

import life.qbic.business.DataSourceException
import life.qbic.business.project.Project
import life.qbic.business.project.subscribe.Subscriber

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
    private final LastChangedDateDataSource loadLastChangedDataSource
    private final LoadProjectsOutput output

    /**
     * Default constructor for this use case
     * @param dataSource the data source to be used
     * @param output the output to where results are published
     * @since 1.0.0
     */
    LoadProjects(LoadProjectsDataSource dataSource, LoadProjectsOutput output, LastChangedDateDataSource loadLastChangedDataSource, SubscribedProjectsDataSource subscribedProjectsDataSource) {
        this.dataSource = dataSource
        this.loadLastChangedDataSource = loadLastChangedDataSource
        this.subscribedProjectsDataSource = subscribedProjectsDataSource
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
            List<Project> projects = loadUserProjects()
            output.loadedProjects(projects)
        } catch (DataSourceException dataSourceException) {
            output.failedExecution(dataSourceException.getMessage())
        } catch (Exception e) {
            output.failedExecution("Could not load projects")
        }
    }

    @Override
    void loadUserProjectsFor(Subscriber subscriber) {
        try {
            List<Project> projects = loadUserProjects()
            loadSubscriptionInformationInto(projects, subscriber)
            loadLastChangedInformationInto(projects)
            output.loadedProjects(projects)
        } catch (DataSourceException dataSourceException) {
            output.failedExecution(dataSourceException.getMessage())
        } catch (Exception e) {
            output.failedExecution("Could not load projects")
            e.printStackTrace()
        }
    }

    private List<Project> loadUserProjects() {
        List<Project> projects = dataSource.fetchUserProjects()
        return projects
    }

    /**
     * Loads subscription information into a list of projects
     * @param projects
     * @param subscriber
     */
    private void loadSubscriptionInformationInto(Iterable<Project> projects, Subscriber subscriber) {
/*        if (! subscribedProjectsDataSource) {
            String message = "Tried to load subscription information without data source."
            throw new IllegalStateException(message)
        }*/
        List<String> subscribedProjectCodes = subscribedProjectsDataSource.findSubscribedProjectCodesFor(subscriber)
        projects.each {it.hasSubscription = subscribedProjectCodes.contains(it.code)}
    }

    private void loadLastChangedInformationInto(Iterable<Project> projects) {
        projects.each { it.lastChanged = loadLastChangedDataSource.getLatestChange(it.code) }
    }
}
