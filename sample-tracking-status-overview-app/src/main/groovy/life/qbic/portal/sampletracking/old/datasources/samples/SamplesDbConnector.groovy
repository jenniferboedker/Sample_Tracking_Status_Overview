package life.qbic.portal.sampletracking.old.datasources.samples

import groovy.util.logging.Log4j2
import life.qbic.business.DataSourceException
import life.qbic.business.project.load.LastChangedDateDataSource
import life.qbic.business.samples.count.CountSamplesDataSource
import life.qbic.business.samples.download.DownloadSamplesDataSource
import life.qbic.business.samples.info.SampleStatusDataSource
import life.qbic.datamodel.samples.Status
import life.qbic.portal.sampletracking.data.database.ConnectionProvider
import life.qbic.portal.sampletracking.old.services.sample.SampleTracking.TrackedSample
import life.qbic.portal.sampletracking.old.services.sample.SampleTrackingService

import java.time.Instant

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
class SamplesDbConnector implements CountSamplesDataSource, DownloadSamplesDataSource, LastChangedDateDataSource, SampleStatusDataSource {
    private final ConnectionProvider connectionProvider
    private final SampleTrackingService sampleTrackingService

    /**
     * Creates a database connector that will connect to the database using the provided connection
     * provider.
     * @param connectionProvider the connection provider providing the connections that are used to
     * connect to the database
     * @since 1.0.0
     */
    SamplesDbConnector(ConnectionProvider connectionProvider, SampleTrackingService sampleTrackingService) {
        this.connectionProvider = connectionProvider
        this.sampleTrackingService = sampleTrackingService
    }

    /**
     * {@inheritDoc}
     * @since 1.0.0
     */
    @Override
    List<String> fetchSampleCodesFor(String projectCode, Status status) {
        List<String> sampleCodes = new ArrayList<>()

        List samples = sampleTrackingService.requestProjectSamplesStatus(projectCode)

        samples.each { sample ->
            if (sample.status == status.toString()) {
                sampleCodes.add(sample.sampleCode)
            }
        }

        return sampleCodes
    }


    /**
     * Given a project code, returns all sample codes with that project
     * @param projectCode
     * @return a list of sample codes for the given project
     */
    @Override
    List<String> fetchSampleCodesFor(String projectCode) {
        List<String> sampleCodes = new ArrayList<>()

        List trackedSamples = sampleTrackingService.requestProjectSamplesStatus(projectCode)

        trackedSamples.each { trackedSample ->
            sampleCodes << trackedSample.sampleCode
        }
        return sampleCodes
    }


    /**
     * {@inheritDoc}
     * <p><b><i>PLEASE NOTE: In case multiple statuses were entered at the same time,
     * this method adds all statuses to the returned list!</i></b></p>
     * @since 1.0.0
     */
    @Override
    List<Status> fetchSampleStatusesForProject(String projectCode) throws DataSourceException {
        List<Status> statuses = new ArrayList<>()

        List trackedSamples = sampleTrackingService.requestProjectSamplesStatus(projectCode)
        trackedSamples.each { trackedSample ->
            parseStatus(trackedSample.status).ifPresent({ statuses.add(it) })
        }
        return statuses
    }

    /**
     * Parses a string status to a {@link Status} in an optional. The optional is empty if
     * the status cannot be parsed
     * @param status A status as string
     * @return an Optional of a status
     */
    private Optional<Status> parseStatus(String status) {
        try {
            Status parsedStatus = Status.valueOf(status)
            return Optional.of(parsedStatus)
        } catch (Exception e) {
            log.error(e.getMessage(), e)
            return Optional.empty()
        }
    }
    /**
     * Keep in mind this will return any sample events and not only sample status updates
     * {@inheritDoc}
     * @since 1.0.0
     */
    @Override
    Instant getLatestChange(String projectCode) throws DataSourceException {
        Instant latest = Instant.MIN

        List<TrackedSample> samples = sampleTrackingService.requestProjectSamplesStatus(projectCode)

        samples.each {sample ->
            if(Instant.parse(sample.validSince).isAfter(latest)){
                latest = Instant.parse(sample.validSince)
            }
        }
        return latest
    }

    @Override
    Map<String, Status> fetchSampleStatusesFor(Collection<String> sampleCodes) {
        Map<String, Status> sampleCodesToStatus = new HashMap<>()

        sampleCodes.each {sampleCode ->
            Optional sample = sampleTrackingService.requestSampleStatus(sampleCode)

            sample.ifPresent({
                Optional status = parseStatus(sample.get().status)
                status.ifPresent({
                    sampleCodesToStatus.put(sample.get().sampleCode, status.get())
                })
            })
        }

        return sampleCodesToStatus
    }

}
