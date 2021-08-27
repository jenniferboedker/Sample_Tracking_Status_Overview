package life.qbic.business.samples.info

import life.qbic.business.DataSourceException
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
  private final GetSamplesInfoOutput output

  /**
   * Default constructor for this use case
   * @param samplesDataSource the data source used to fetch sample codes with a certain status
   * @param infoDataSource the data source used to add metadata (e.g. name) to those sample codes
   * @param output the output to where results are published
   * @since 1.0.0
   */
  GetSamplesInfo(DownloadSamplesDataSource samplesDataSource, GetSamplesInfoDataSource infoDataSource, GetSamplesInfoOutput output) {
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
    try {
        def sampleCodes = samplesDataSource.fetchSampleCodesFor(projectCode, status)
        println "first succeeded"
        def sampleCodesToNames = infoDataSource.fetchSampleNamesFor(sampleCodes)
        println "second succeeded"
        
      output.samplesWithNames(projectCode, status, sampleCodesToNames)
    } catch (DataSourceException dataSourceException) {
      output.failedExecution(dataSourceException.getMessage())
    } catch (Exception e) {
      output.failedExecution("Could not fetch sample codes with available data.")
    }
  }
}
