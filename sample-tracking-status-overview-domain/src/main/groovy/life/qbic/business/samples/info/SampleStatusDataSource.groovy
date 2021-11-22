package life.qbic.business.samples.info

import life.qbic.business.DataSourceException
import life.qbic.datamodel.samples.Status

/**
 * <b>Provides information on samples and their status</b>
 *
 *
 * @since 1.0.0
 */
interface SampleStatusDataSource {

    /**
     * Retrieves sample statuses for a collection of provided sample codes
     * @param sampleCodes the sample codes of samples that should be checked for their sample status
     * @return a map containing statuses for every sample code as key
     * @throws DataSourceException in case of a technical failure
     * @since 1.0.0
     */
    Map<String, Status> fetchSampleStatusesFor(Collection<String> sampleCodes) throws DataSourceException
}
