package life.qbic.business.samples.count

import life.qbic.business.DataSourceException

/**
 * <b>Load samples</b>
 *
 * <p>This use case returns all sample codes of a project that have a specific status.</p>
 *
 * @since 1.0.0
 */
class LoadSamples implements CountSamplesInput{
    private final CountSamplesDataSource dataSource
    private final CountSamplesOutput output

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
    void countSamples(String projectCode) {
        try {
            List sampleStatuses = dataSource.getCurrentSampleStatuses(projectCode)
            int receivedAmount = countReceivedSamples(sampleStatuses)
            output.countedSamples(sampleStatuses.size(), receivedAmount)
        } catch (DataSourceException dataSourceException) {
            output.failedExecution(dataSourceException.getMessage())
        } catch (Exception e) {
            output.failedExecution("Could not load samples")
        }
    }



}
