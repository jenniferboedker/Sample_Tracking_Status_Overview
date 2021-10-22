package life.qbic.business.samples.count

import life.qbic.business.DataSourceException
import life.qbic.datamodel.samples.Status

/**
 * <b>Count samples</b>
 *
 * <p>This use case counts samples of a project and the subset of samples that have a specific status.</p>
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

  private List<Status> sampleStatuses
  private int totalSampleCount
  private String projectCode

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

  @Override
  void countSamplesPerStatus(String projectCode) {
    try {
      sampleStatuses = dataSource.fetchSampleStatusesForProject(projectCode)

      totalSampleCount = sampleStatuses.size()
      this.projectCode = projectCode

      int received = countSamplesFromStatus(Status.SAMPLE_RECEIVED)
      int failedQc = sampleStatuses.findAll { it == Status.SAMPLE_QC_FAIL }.size()
      int passedQc = countSamplesFromStatus(Status.SAMPLE_QC_PASS)
      int libraryPrep = countSamplesFromStatus(Status.LIBRARY_PREP_FINISHED)
      int availableData = countSamplesFromStatus(Status.DATA_AVAILABLE)

      StatusCount statusCount = new StatusCount(projectCode, received, passedQc, failedQc, libraryPrep, availableData, totalSampleCount)
      output.countedSamples(statusCount)
    } catch (DataSourceException dataSourceException) {
      output.failedExecution(dataSourceException.getMessage())
    } catch (Exception ignored) {
      output.failedExecution("Could not count samples.")
    }
  }

  private int countSamplesFromStatus(Status status) {
    int receivedIndex = statusesInOrder.indexOf(status)
    // statuses that are not considered in the ordered list return -1, meaning the sample is not counted
    return sampleStatuses.findAll { statusesInOrder.indexOf(it) >= receivedIndex }.size()
  }
}