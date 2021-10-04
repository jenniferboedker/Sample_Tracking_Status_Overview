package life.qbic.business.project.load

import life.qbic.business.DataSourceException
import life.qbic.datamodel.dtos.projectmanagement.Project
import java.time.Instant


/**
 * <b>Provides information about the time of latest changes for a project</b>
 *
 * <p>This interface is used by {@link LoadProjects}</p>
 *
 * @since 1.0.0
 */
interface LastChangedDateDataSource {

    /**
     * Returns the Timestamp of the latest changed sample of a project
     * @return a Timestamp signifying the latest change to a sample status of the project
     * @param projectCode code of the project
     * @since 1.0.0
     * @throws DataSourceException in case of a technical error with the data source
     */
    Instant getLatestChange(String projectCode) throws DataSourceException
}
