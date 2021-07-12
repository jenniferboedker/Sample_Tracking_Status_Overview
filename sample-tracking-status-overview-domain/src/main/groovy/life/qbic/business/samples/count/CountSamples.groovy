package life.qbic.business.samples.count

import life.qbic.business.DataSourceException
import life.qbic.datamodel.samples.Status

/**
 * <b>Count samples</b>
 *
 * <p>This use case counts samples of a project and the subset of samples that have been received at the lab.</p>
 *
 * @since 1.0.0
 */
class CountSamples implements CountSamplesInput{
  private final CountSamplesDataSource dataSource
  private final CountSamplesOutput output
  // used to be able to count samples for having completed a status when they are in a further step
  private final List<Status> statusesInOrder = [Status.METADATA_REGISTERED, Status.SAMPLE_RECEIVED,
    Status.SAMPLE_QC_FAIL, Status.SAMPLE_QC_PASS,
    Status.LIBRARY_PREP_FINISHED, Status.DATA_AVAILABLE]

  /**
   * Default constructor for this use case
   * @param dataSource the data source to be used
   * @param output the output to where results are published
   * @since 1.0.0
   */
  CountSamples(CountSamplesDataSource dataSource, CountSamplesOutput output) {
    this.dataSource = dataSource
    this.output = output
  }

  /**
   * This method calls the output interface with the number of all samples in a project as
   * well as the subset of samples received by a lab.
   * In case of failure the output interface failure method is called.
   * @since 1.0.0
   */
  @Override
  void countReceivedSamples(String projectCode) {
    try {
      List sampleStatuses = dataSource.fetchSampleStatusesForProject(projectCode)
      int receivedAmount = countReceivedSamplesFromStatus(sampleStatuses)
      output.countedReceivedSamples(sampleStatuses.size(), receivedAmount)
    } catch (DataSourceException dataSourceException) {
      output.failedExecution(dataSourceException.getMessage())
    } catch (Exception e) {
      output.failedExecution("Could not count received samples.")
    }
  }

  private int countReceivedSamplesFromStatus(List<Status> sampleStatuses) {
    int res = 0
    int receivedIndex = statusesInOrder.indexOf(Status.SAMPLE_RECEIVED)
    for (Status status : sampleStatuses) {
      // if an index is notfound, -1 is returned, resulting in that sample not being counted
      if (statusesInOrder.indexOf(status) >= receivedIndex) {
        res++
      }
    }
    return res
  }



}
