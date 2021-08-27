package life.qbic.business.samples.count

import life.qbic.business.DataSourceException
import life.qbic.datamodel.samples.Status

/**
 * <b>Provides project identifiers</b>
 *
 * <p>This interface is used by {@link CountSamples}</p>
 *
 * @since 1.0.0
 */
interface CountSamplesDataSource {

    /**
     * Returns all current sample statuses of a project
     * @param projectCode the code of a project
     * @return List of Statuses for samples of a project
     * @since 1.0.0
     * @throws DataSourceException in case of a technical error with the data source
     */
    List<Status> fetchSampleStatusesForProject(String projectCode) throws DataSourceException
}
