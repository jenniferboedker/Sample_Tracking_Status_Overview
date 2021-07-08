package life.qbic.business.samples.load

import life.qbic.business.DataSourceException
import life.qbic.datamodel.samples.Status

/**
 * <b>Load samples</b>
 *
 * <p>This use case returns all sample codes of a project that have a specific status.</p>
 *
 * @since 1.0.0
 */
class LoadSamples implements LoadSamplesInput{
    private final LoadSamplesDataSource dataSource
    private final LoadSamplesOutput output

    /**
     * Default constructor for this use case
     * @param dataSource the data source to be used
     * @param output the output to where results are published
     * @since 1.0.0
     */
    LoadSamples(LoadSamplesDataSource dataSource, LoadSamplesOutput output) {
        this.dataSource = dataSource
        this.output = output
    }

    /**
     * This method calls the output interface with all sample codes found.
     * In case of failure the output interface failure method is called.
     * @since 1.0.0
     */
    @Override
    void loadSamples(String projectCode, Status sampleStatus) {
        try {
            println "x"
            List sampleCodes = dataSource.fetchSamplesWithCurrentStatus(projectCode, sampleStatus)
            println "y"
            output.loadedSamples(sampleCodes)
        } catch (DataSourceException dataSourceException) {
            output.failedExecution(dataSourceException.getMessage())
        } catch (Exception e) {
            output.failedExecution("Could not load samples")
        }
    }



}
