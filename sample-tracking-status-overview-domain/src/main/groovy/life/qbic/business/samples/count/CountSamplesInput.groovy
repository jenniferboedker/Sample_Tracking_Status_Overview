package life.qbic.business.samples.count

/**
 * <b>Input interface for the {@link CountSamples} feature</b>
 *
 * <p>Provides methods to trigger the counting of samples for one project</p>>
 *
 * @since 1.0.0
 */
interface CountSamplesInput {

    /**
     * This method calls the output interface with the number of samples in the project
     * and the number of samples that have been received.
     * In case of failure the output interface failure method is called.
     * @since 1.0.0
     */
    void countReceivedSamples(String projectCode)
}
