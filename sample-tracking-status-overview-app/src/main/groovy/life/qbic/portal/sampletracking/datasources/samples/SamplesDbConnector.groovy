package life.qbic.portal.sampletracking.datasources.samples

import groovy.util.logging.Log4j2
import life.qbic.business.DataSourceException
import life.qbic.business.samples.count.CountSamplesDataSource
import life.qbic.datamodel.samples.Status
import life.qbic.portal.sampletracking.datasources.database.ConnectionProvider

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

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
@Log4j2
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

    /**
     * {@inheritDoc}
     * @since 1.0.0
     * We need to fetch all samples initially, since only the latest status counts. This only remains true, while
     * Status.DATA_AVAILABLE is the last status a sample can reach.
     */
    @Override
    List<String> fetchSampleCodesWithData(String projectCode) throws DataSourceException {
        String queryTemplate = Query.fetchLatestSampleEntries()
        Connection connection = connectionProvider.connect()
        List<Status> statuses = new ArrayList<>()
        String sqlRegex = "${projectCode}%"
        connection.withCloseable {
            PreparedStatement preparedStatement = it.prepareStatement(queryTemplate)
            preparedStatement.setString(1, sqlRegex)
            ResultSet resultSet = preparedStatement.executeQuery()
            while (resultSet.next()) {
                String sampleCode = resultSet.getString("sample_id")
                String sampleStatusString = resultSet.getString("sample_status")
                String arrivalTime = resultSet.getString("arrival_time")
                Status sampleStatus
                try {
                    sampleStatus = Status.valueOf(sampleStatusString)
                } catch(IllegalArgumentException statusNotFound) {
                    // The status in the database is invalid. This should never be the case!
                    log.error("Could not parse status $sampleStatusString for $sampleCode at $arrivalTime", statusNotFound)
                    throw new DataSourceException("Retrieval of sample statuses failed for sample $sampleCode")
                }
                if(Status.DATA_AVAILABLE.equals(sampleStatus)) {
                    statuses.add(sampleCode)
                }
            }
        }
        return statuses
    }

    /**
     * {@inheritDoc}
     * <p><b><i>PLEASE NOTE: In case multiple statuses were entered at the same time,
     * this method adds all statuses to the returned list!</i></b></p>
     * @since 1.0.0
     */
    @Override
    List<Status> fetchSampleStatusesForProject(String projectCode) throws DataSourceException {
        String queryTemplate = Query.fetchLatestSampleEntries()
        Connection connection = connectionProvider.connect()
        List<Status> statuses = new ArrayList<>()
        String sqlRegex = "$projectCode%"
        connection.withCloseable {
            PreparedStatement preparedStatement = it.prepareStatement(queryTemplate)
            preparedStatement.setString(1, sqlRegex)
            ResultSet resultSet = preparedStatement.executeQuery()
            while (resultSet.next()) {
                String sampleCode = resultSet.getString("sample_id")
                String sampleStatusString = resultSet.getString("sample_status")
                String arrivalTime = resultSet.getString("arrival_time")
                Status sampleStatus
                try {
                    sampleStatus = Status.valueOf(sampleStatusString)
                } catch(IllegalArgumentException statusNotFound) {
                    // The status in the database is invalid. This should never be the case!
                    log.error("Could not parse status $sampleStatusString for $sampleCode at $arrivalTime", statusNotFound)
                    throw new DataSourceException("Retrieval of sample statuses failed for sample $sampleCode")
                }
                statuses.add(sampleStatus)
            }
        }
        return statuses
    }

    private class Query {
        /**
         * Generates a query with one wildcard ? that can be filled with a sql match to
         * filter by sample_id.
         * <p>The returned query provides all rows for which the arrival_time matches the
         * latest arrival_time recorded for this sample_id.</p>
         * @return a query template
         * @since 1.0.0
         */
        private static String fetchLatestSampleEntries() {
            /*
            The filter criteria to avoid applying the query to the whole table.
            Replace `?` with your matching sample_id.
             */
            final String filterCriteria = "WHERE sample_id LIKE ? AND sample_id NOT LIKE \"%ENTITY%\""
            /*
             * This query constructs a table in the form of
             # sample_id| MAX(arrival_time)
             QSTTS030A8 | 2021-05-11 15:05:00
             QSTTS029A5 | 2021-05-11 15:05:00
             QSTTS028AV | 2021-05-11 15:05:00
             QSTTS027AN | 2021-05-11 15:05:00
             QSTTS026AF | 2021-05-11 15:05:00
             QSTTS025A7 | 2021-05-11 15:05:00
             QSTTS024AX | 2021-05-11 15:05:00
             QSTTS023AP | 2021-05-11 15:05:00
             QSTTS022AH | 2021-05-11 15:05:00
             */
            final String latestEditQuery = "SELECT sample_id, MAX(arrival_time) as arrival_time FROM samples_locations $filterCriteria GROUP BY sample_id"

            /*
             * This query filters the samples_locations table and only returns samples whose
             * arrival_time matches the latest arrival_time.
             * We do need this since we cannot assume that there is only one entry with the latest time.
             */
            final String latestEntriesQuery = "SELECT samples_locations.* FROM samples_locations " +
                    "INNER JOIN ($latestEditQuery) AS latest_arrivals " +
                    "ON latest_arrivals.sample_id = samples_locations.sample_id " +
                    "AND latest_arrivals.arrival_time = samples_locations.arrival_time;"

            return latestEntriesQuery
        }
    }
}
