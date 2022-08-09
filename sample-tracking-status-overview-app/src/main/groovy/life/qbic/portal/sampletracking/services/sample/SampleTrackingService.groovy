package life.qbic.portal.sampletracking.services.sample

/**
 * <b>Sample Tracking Service Connection</b>
 *
 * <p>Interface to the sample tracking service, which allows to request sample status information</p>
 *
 * @since 1.1.1
 */
interface SampleTrackingService {

    /**
     * Requests the sample tracking status for a sample by code
     * @param sampleCode The sample for which the status will be requested
     * @return
     */
    Optional<SampleTracking.TrackedSample> requestSampleStatus(String sampleCode)

}
