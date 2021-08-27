package life.qbic.business.samples.count

/**
 * <b>Output interface for the {@link CountSamples} feature</b>
 *
 * <p>Publishes results from counting samples for one project</p>
 *
 * @since 1.0.0
 */
interface CountSamplesOutput {

    /**
     * To be called when the execution of the samples counting failed
     * @param reason the reason why the samples retrieval failed
     * @since 1.0.0
     */
    void failedExecution(String reason)

    /**
     * To be called after successfully counting received samples for the provided code.
     * @param number of all samples and samples that have been received by the lab
     * @param projectCode the code of the project samples were counted for
     * @since 1.0.0
     */
    void countedReceivedSamples(String projectCode, int allSamples, int receivedSamples)

    /**
     * To be called after successfully counting failed QC samples for the provided code.
     * @param number of all samples and samples that have failed QC
     * @param projectCode the code of the project samples were counted for
     * @since 1.0.0
     */
    void countedFailedQcSamples(String projectCode, int allSamples, int failedQcSamples)

    /**
     * To be called after successfully counting samples with available data for the provided code.
     * @param number of all samples and samples that have available data
     * @param projectCode the code of the project samples were counted for
     * @since 1.0.0
     */
    void countedAvailableSampleData(String projectCode, int allSamples, int availableData)
}
