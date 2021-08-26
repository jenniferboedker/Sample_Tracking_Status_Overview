package life.qbic.business.samples.info

import life.qbic.datamodel.samples.Status

/**
 * <b>Output interface for the {@link DownloadSamples} feature</b>
 *
 * <p>Returns results from finding sample codes with downloadable data for one project</p>
 *
 * @since 1.0.0
 */
interface GetSamplesInfoOutput {

    /**
     * To be called when the execution of the fetching sample codes failed
     * @param reason the reason why the samples retrieval failed
     * @since 1.0.0
     */
    void failedExecution(String reason)

    /**
     * To be called after successfully fetching sample codes with data for the provided code.
     * @param projectCode the code of the project samples were counted for
     * @param status the code of the project samples were counted for
     * @param sampleCodesToNames list of sample codes with available data
     * @since 1.0.0
     */
    void samplesWithNames(String projectCode, Status status, Map<String, String> sampleCodesToNames)

}
