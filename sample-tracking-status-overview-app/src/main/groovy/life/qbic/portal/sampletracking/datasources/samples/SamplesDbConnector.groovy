package life.qbic.portal.sampletracking.datasources.samples

import life.qbic.business.DataSourceException
import life.qbic.business.samples.count.CountSamplesDataSource
import life.qbic.datamodel.samples.Status
import life.qbic.portal.sampletracking.datasources.database.ConnectionProvider

/**
 * <b>Database connector to the sample tracking database</b>
 *
 * <p>This connector provides functionality to receive sample information for samples tracked by
 * the sample tracking system that is in place at QBiC.</p>
 * <p>Furthermore the connector implements the {@link CountSamplesDataSource} and provides information
 * that is required by the respective use case</p>
 *
 * @since 1.0.0
 */
class SamplesDbConnector implements CountSamplesDataSource {
    private final ConnectionProvider connectionProvider

    /**
     * Creates a database connector that will connect to the database using the provided connection
     * provider.
     * @param connectionProvider the connection provider providing the connections that are used to
     * connect to the database
     * @since 1.0.0
     */
    SamplesDbConnector(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider
    }


    @Override
    List<Status> fetchSampleStatusesForProject(String projectCode) throws DataSourceException {
        final String sqlQuery = "SELECT sample_id, sample_status FROM samples_locations WHERE sample_id LIKE \"$projectCode%\" AND sample_id NOT LIKE \"%ENTITY%\""
        /* TODO
            1. select (sample_id, sample_status)
            2. group by sample_id
            3. find newest sample_status
            4. return list of sample_statuses
         */
        return null
    }
}
