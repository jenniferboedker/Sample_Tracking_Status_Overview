package life.qbic.business.samples.info

import life.qbic.business.samples.Sample
import life.qbic.datamodel.samples.Status

/**
 * <b>Output interface for the {@link GetSamplesInfo} feature</b>
 *
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
     * @param samples a collection of samples with names
     * @since 1.0.0
     */
    void samplesWithNames(Collection<Sample> samples)

}
