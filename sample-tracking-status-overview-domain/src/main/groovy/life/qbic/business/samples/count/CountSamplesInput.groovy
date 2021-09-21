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
     * and the number of samples are within (or have passed) a specific status.
     * In case of failure the output interface failure method is called.
     *
     * @param projectCode a code specifying the samples that should be considered
     * @since 1.0.0
     */
    void countSamplesPerStatus(String projectCode)

}
