package life.qbic.business.samples.info

import life.qbic.datamodel.samples.Status

/**
 * <b>Output interface for the {@link GetSamplesInfo} feature</b>
 *
 * <p>Returns results from finding sample codes with respective names for one type of sample status and one project</p>
 *
 * @since 1.0.0
 */
interface GetSamplesInfoOutput {

    /**
     * To be called when the execution of the fetching sample codes or respective info failed
     * @param reason the reason why the sample or sample info retrieval failed
     * @since 1.0.0
     */
    void failedExecution(String reason)

    /**
     * To be called after successfully fetching sample codes with respective sample names for the provided project and status.
     * @param projectCode the code of the project samples should be returned for
     * @param status the status of the samples that should be returned
     * @param sampleCodesToNames list of sample codes with names
     * @since 1.0.0
     */
    void samplesWithNames(String projectCode, Status status, Map<String, String> sampleCodesToNames)

}
