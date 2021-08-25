package life.qbic.business.samples.info

import life.qbic.business.DataSourceException
import life.qbic.datamodel.samples.Status

/**
 * <b>Provides sample information of samples of a current status</b>
 *
 * <p>This interface is used by {@link x}</p>
 *
 * @since 1.0.0
 */
interface GetSamplesInfoDataSource {

    /**
     * Given a project code and a status, returns a map of sample codes with that current sample status and the samples' names
     * @param projectCode the code of a project
     * @param status the sample status of the wanted samples
     * @return Map with sample codes as keys and sample names as values
     * @since 1.0.0
     */
    Map<String, String> fetchSampleInfosFor(String projectCode, Status status) throws DataSourceException

}
