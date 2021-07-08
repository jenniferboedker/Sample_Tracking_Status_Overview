package life.qbic.business.samples.load

/**
 * <b>Input interface for the {@link LoadSamples} feature</b>
 *
 * <p>Provides methods to trigger the loading of samples for one project</p>>
 *
 * @since 1.0.0
 */
interface LoadSamplesInput {

    /**
     * This method calls the output interface with all samples found.
     * In case of failure the output interface failure method is called.
     * @since 1.0.0
     */
    void loadSamples(String projectCode)
}
