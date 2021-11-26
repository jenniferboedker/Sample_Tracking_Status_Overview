package life.qbic.business.samples.info

import life.qbic.business.DataSourceException
import life.qbic.business.samples.Sample
import life.qbic.business.samples.download.DownloadSamplesDataSource
import life.qbic.datamodel.samples.Status

/**
 * <b>Get information of samples</b>
 *
 * <p>This use case returns codes and respective names of samples with a requested status of a project.</p>
 *
 * @since 1.0.0
 */
class GetSamplesInfo implements GetSamplesInfoInput {
  
  private final DownloadSamplesDataSource samplesDataSource
  private final GetSamplesInfoDataSource infoDataSource
  private final SampleStatusDataSource statusDataSource
  private final GetSamplesInfoOutput output

  /**
   * Default constructor for this use case
   * @param samplesDataSource the data source used to fetch sample codes with a certain status
   * @param infoDataSource the data source used to add metadata (e.g. name) to those sample codes
   * @param output the output to where results are published
   * @since 1.0.0
   */
  GetSamplesInfo(SampleStatusDataSource statusDataSource, DownloadSamplesDataSource samplesDataSource, GetSamplesInfoDataSource infoDataSource, GetSamplesInfoOutput output) {
    this.statusDataSource = statusDataSource
    this.samplesDataSource = samplesDataSource
    this.infoDataSource = infoDataSource
    this.output = output
  }

  /**
   * This method calls the output interface with the codes and names of the samples in a project that have a provided status.
   * In case of failure the output interface failure method is called.
   * @since 1.0.0
   */
  @Override
  void requestSampleInfosFor(String projectCode, Status status) {
    Objects.requireNonNull(status, "Tried to request sample infos without providing a status.")
    Objects.requireNonNull(projectCode, "Tried to request sample infos without providing a projectCode.")
    try {
        def sampleCodes = samplesDataSource.fetchSampleCodesFor(projectCode, status)
        def sampleCodesToNames = infoDataSource.fetchSampleNamesFor(sampleCodes)

      List<Sample> samplesWithNames = buildSamples(sampleCodesToNames, status)
      output.samplesWithNames(samplesWithNames)
    } catch (DataSourceException dataSourceException) {
      output.failedExecution(dataSourceException.getMessage())
    } 
  }

  /**
   * This method calls the output interface with the samples for the provided project code
   * @param projectCode a code specifying the samples that should be considered
   * @since 1.0.0
   */
  @Override
  void requestSampleInfosFor(String projectCode) {
    Objects.requireNonNull(projectCode, "Tried to request sample infos without providing a projectCode.")
    try {
      List<String> sampleCodes = samplesDataSource.fetchSampleCodesFor(projectCode)
      if (sampleCodes.isEmpty()) {
        //ToDo should this be notified to the user?
        output.samplesWithNames([])
        return
      }
      def sampleCodesToNames = infoDataSource.fetchSampleNamesFor(sampleCodes)
      def sampleCodesToStatus = statusDataSource.fetchSampleStatusesFor(sampleCodes)
      List<Sample> samplesWithNames = buildSamples(sampleCodesToNames, sampleCodesToStatus)
      output.samplesWithNames(samplesWithNames)
    } catch (DataSourceException dataSourceException) {
      output.failedExecution(dataSourceException.getMessage())
    }
  }

  private static List<Sample> buildSamples(Map<String, String> codesToNames, Status status) {
    List<Sample> samples = codesToNames.entrySet().stream()
            .map({
              return new Sample(it.key, it.value, status)
            }).collect()
    return Optional.ofNullable(samples).orElse([])
  }

  private static List<Sample> buildSamples(Map<String, String> codesToNames, Map<String, Status> codesToStatus) {
    if (codesToNames.keySet() != codesToStatus.keySet()) {
      throw new RuntimeException("Tried to get samples without sufficient information.")
    }
    List<Sample> samples = codesToNames.entrySet().stream()
            .map({
              String code = it.key
              String name = it.value ?: ""
              return new Sample(code, name, codesToStatus.get(it.key))
            }).collect()
    return Optional.ofNullable(samples).orElse([])
  }
}
