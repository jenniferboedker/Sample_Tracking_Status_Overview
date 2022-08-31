package life.qbic.portal.sampletracking.old.components.sampleoverview

import life.qbic.business.samples.info.GetSamplesInfoInput

/**
 * <b>This controller connects to the sample loading.</b>
 *
 * @since 1.0.0
 */
class SampleOverviewController {

    private final GetSamplesInfoInput getSamplesInfoInput

    SampleOverviewController(GetSamplesInfoInput getSamplesInfoInput) {
        this.getSamplesInfoInput = getSamplesInfoInput
    }

    /**
     * Triggers the loading of samples for the given project code
     * @param projectCode identifies the project, samples are loaded for
     * @since 1.0.0
     */
    void getSamplesFor(String projectCode) {
        getSamplesInfoInput.requestSampleInfosFor(projectCode)
    }
}
