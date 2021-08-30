package life.qbic.business.samples.info

import life.qbic.datamodel.samples.Status

/**
 * <b>Input interface for the {@link GetSamplesInfo} feature</b>
 *
 * <p>Provides methods to trigger the fetching of sample codes and names of samples with provided status for one project</p>
 *
 * @since 1.0.0
 */
interface GetSamplesInfoInput {

    /**
     * <p>This method calls the output interface with the codes and names of samples in the project
     * that have a certain status</p>
     * <p>In case of failure the output interface failure method is called.</p>
     *
     * @param projectCode a code specifying the samples that should be considered
     * @param status a kind of sample status matching a category in the sample tracking database
     * @since 1.0.0
     */
    void requestSampleInfosFor(String projectCode, Status status)

}
