package life.qbic.business.project.load
/**
 * <b>Load projects</b>
 *
 * <p>This use case loads all projects for a specific person.</p>
 *
 * @since 1.0.0
 */
class LoadProjects implements LoadProjectsInput{
    private final LoadProjectsInfoDataSource dataSource
    private final LoadProjectsOutput output

    /**
     * Default constructor for this use case
     * @param dataSource the data source to be used
     * @param output the output to where results are published
     * @since 1.0.0
     */
    LoadProjects(LoadProjectsInfoDataSource dataSource, LoadProjectsOutput output) {
        this.dataSource = dataSource
        this.output = output
    }

    @Override
    void execute(String identifier) {
        //TODO get all project codes from openBIS data source
        //TODO get all project information from database data source
        //TODO pass result to output
    }
}
