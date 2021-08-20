package life.qbic.business.samples.download

import life.qbic.business.DataSourceException
import life.qbic.datamodel.samples.Status

/**
 * <b>Provides sample codes</b>
 *
 * <p>This interface is used by {@link DownloadSamples}</p>
 *
 * @since 1.0.0
 */
interface DownloadSamplesDataSource {

    /**
     * Given a project code and a status, returns all sample codes with that current sample status
     * @param projectCode the code of a project
     * @param status the sample status of the wanted samples
     * @return List of sample codes of a project that currently have the status in questions
     * @since 1.0.0
     */
    List<String> fetchSampleCodesFor(String projectCode, Status status)
}
