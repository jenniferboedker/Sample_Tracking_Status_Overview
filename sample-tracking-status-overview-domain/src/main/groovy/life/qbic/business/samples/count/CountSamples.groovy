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
      sampleStatuses = dataSource.fetchSampleStatusesForProject(projectCode)
      // counts samples that have AT LEAST this status (or a later one)
      int receivedAmount = countSamplesFromStatus(Status.SAMPLE_RECEIVED)
      output.countedReceivedSamples(projectCode, sampleStatuses.size(), receivedAmount)
    } catch (DataSourceException dataSourceException) {
      output.failedExecution(dataSourceException.getMessage())
    } catch (Exception ignored) {
      output.failedExecution("Could not count received samples.")
    }
  }

  /**
   * This method calls the output interface with the number of all samples in a project as
   * well as the subset of samples which had a failed quality control
   * In case of failure the output interface failure method is called.
   * @since 1.0.0
   */
  @Override
  void countQcFailedSamples(String projectCode) {
    try {
      sampleStatuses = dataSource.fetchSampleStatusesForProject(projectCode)
      int receivedAmount = sampleStatuses.findAll { it == Status.SAMPLE_QC_FAIL }.size()
      output.countedFailedQcSamples(projectCode, sampleStatuses.size(), receivedAmount)
    }catch (Exception ignored) {
      output.failedExecution("Could not count failed qc samples.")
    }
  }

  @Override
  void countAvailableDataSamples(String projectCode) {
    try {
      sampleStatuses = dataSource.fetchSampleStatusesForProject(projectCode)
      int availableData = countSamplesFromStatus(Status.DATA_AVAILABLE)
      output.countedAvailableSampleData(projectCode, sampleStatuses.size(), availableData)
    } catch (Exception e) {
      output.failedExecution(e.getMessage())
    }
  }

  @Override
  void countLibraryPrepFinishedSamples(String projectCode) {
    try {
      sampleStatuses = dataSource.fetchSampleStatusesForProject(projectCode)
      int libraryPrepFinished = countSamplesFromStatus(Status.LIBRARY_PREP_FINISHED)
      output.countedLibraryPrepFinishedSamples(projectCode, sampleStatuses.size(), libraryPrepFinished)
    } catch (Exception e) {
      output.failedExecution(e.getMessage())
    }
  }

  private int countSamplesFromStatus(Status status) {
    int receivedIndex = statusesInOrder.indexOf(status)
    // statuses that are not considered in the ordered list return -1, meaning the sample is not counted
    return sampleStatuses.findAll { statusesInOrder.indexOf(it) >= receivedIndex }.size()
  }
}