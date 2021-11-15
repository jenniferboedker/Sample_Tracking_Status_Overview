package life.qbic.business.samples.info

import life.qbic.datamodel.samples.Status

/**
 * <b><short description></b>
 *
 * <p><detailed description></p>
 *
 * @since <version tag>
 */
interface SampleStatusDataSource {
    Map<String, Status> fetchSampleStatusesFor(Collection<String> sampleCodes)
}
