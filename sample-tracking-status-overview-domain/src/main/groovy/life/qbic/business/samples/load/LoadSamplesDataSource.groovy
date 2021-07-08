package life.qbic.business.samples.load

import life.qbic.business.DataSourceException
import life.qbic.datamodel.samples.Status


/**
 * <b>Provides project identifiers</b>
 *
 * <p>This interface is used by {@link LoadSamples}</p>
 *
 * @since 1.0.0
 */
interface LoadSamplesDataSource {

    /**
     * Loads samples from a project, given a project code and sample status
     * @return a list of samples that are part of this project and currently have the provided status
     * @since 1.0.0
     * @throws DataSourceException in case of a technical error with the data source
     */
    List<String> fetchSamplesWithCurrentStatus(String projectCode, Status status) throws DataSourceException
}
