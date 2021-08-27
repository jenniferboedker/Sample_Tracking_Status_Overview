package life.qbic.business.samples.download

import life.qbic.business.OutputException

/**
 * <b>Output interface for the {@link DownloadSamples} feature</b>
 *
 * <p>Returns results from finding sample codes with downloadable data for one project</p>
 *
 * @since 1.0.0
 */
interface DownloadSamplesOutput {

    /**
     * To be called when the execution of the fetching sample codes failed
     * @param reason the reason why the samples retrieval failed
     * @since 1.0.0
     */
    void failedExecution(String reason) throws OutputException

    /**
     * To be called after successfully fetching sample codes with data for the provided code.
     * @param projectCode the code of the project samples were counted for
     * @param sampleCodes list of sample codes with available data
     * @since 1.0.0
     */
    void foundDownloadableSamples(String projectCode, List<String> sampleCodes) throws OutputException

}
