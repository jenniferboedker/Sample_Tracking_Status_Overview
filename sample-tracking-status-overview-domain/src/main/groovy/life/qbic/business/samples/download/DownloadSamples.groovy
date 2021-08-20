package life.qbic.business.samples.download

import life.qbic.business.DataSourceException
import life.qbic.datamodel.samples.Status

/**
 * <b>Download samples</b>
 *
 * <p>This use case returns samples of a project that have available data attached.</p>
 *
 * @since 1.0.0
 */
class DownloadSamples implements DownloadSamplesInput {
  private final DownloadSamplesDataSource dataSource
  private final DownloadSamplesOutput output
  // used to be able to add samples for having completed the available data status when they are in a further step
  private final List<Status> statusesWithData = [Status.DATA_AVAILABLE]
  private List<String> sampleCodes

  /**
   * Default constructor for this use case
   * @param dataSource the data source to be used
   * @param output the output to where results are published
   * @since 1.0.0
   */
  DownloadSamples(DownloadSamplesDataSource dataSource, DownloadSamplesOutput output) {
    this.dataSource = dataSource
    this.output = output
  }

  /**
   * This method calls the output interface with the codes of the samples in a project that have data attached.
   * In case of failure the output interface failure method is called.
   * @since 1.0.0
   */
  @Override
  void fetchSampleCodesWithData(String projectCode) {
    sampleCodes = new ArrayList<>();
    try {
      for(Status status : statusesWithData) {
        sampleCodes.addAll(dataSource.fetchSampleCodesFor(projectCode, status))
      }
      output.fetchSampleCodesWithData(projectCode, sampleCodes)
    } catch (DataSourceException dataSourceException) {
      output.failedExecution(dataSourceException.getMessage())
    } catch (Exception e) {
      output.failedExecution("Could not fetch sample codes with available data.")
    }
  }
}