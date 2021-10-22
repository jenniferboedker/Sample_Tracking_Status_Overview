package life.qbic.business.samples.count

import life.qbic.business.OutputException

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
    void failedExecution(String reason) throws OutputException
    /**
     * To be called after successfully counting samples for each status for the provided project code.
     * @param statusCount the count of received statuses
     */
    void countedSamples(StatusCount statusCount)
}
