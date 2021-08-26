package life.qbic.business.samples.info

import life.qbic.datamodel.samples.Status

/**
 * <b>Input interface for the {@link DownloadSamples} feature</b>
 *
 * <p>Provides methods to trigger the fetching of sample codes with available data for one project</p>>
 *
 * @since 1.0.0
 */
interface GetSamplesInfoInput {

    /**
     * This method calls the output interface with the codes of samples in the project
     * that have available data attached
     * In case of failure the output interface failure method is called.
     *
     * @param projectCode a code specifying the samples that should be considered
     * @since 1.0.0
     */
    void requestSampleInfosFor(String projectCode, Status status)

}
