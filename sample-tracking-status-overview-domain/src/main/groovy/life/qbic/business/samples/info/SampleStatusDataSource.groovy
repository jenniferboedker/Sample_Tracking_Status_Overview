package life.qbic.business.samples.info

import life.qbic.datamodel.samples.Status

/**
 * <b>Provides information on samples and their status</b>
 *
 *
 * @since 1.0.0
 */
interface SampleStatusDataSource {
    Map<String, Status> fetchSampleStatusesFor(Collection<String> sampleCodes)
}
