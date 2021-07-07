package life.qbic.business.samples.load

/**
 * <b>Output interface for the {@link LoadSamples} feature</b>
 *
 * <p>Publishes results from loading samples for one project</p>
 *
 * @since 1.0.0
 */
interface LoadSamplesOutput {

    /**
     * To be called when the execution of the samples loading failed
     * @param reason the reason why the samples retrieval failed
     * @since 1.0.0
     */
    void failedExecution(String reason)

    /**
     * To be called after successfully loading samples for the provided code.
     * @param samples codes of all samples that could be loaded
     * @since 1.0.0
     */
    void loadedSamples(List<String> samples)
}
