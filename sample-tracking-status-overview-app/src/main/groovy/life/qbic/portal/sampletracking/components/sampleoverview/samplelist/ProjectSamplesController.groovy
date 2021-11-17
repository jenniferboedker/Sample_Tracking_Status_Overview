package life.qbic.portal.sampletracking.components.sampleoverview.samplelist

import life.qbic.business.samples.info.GetSamplesInfoInput

/**
 * <b>This controller connects to the sample loading.</b>
 *
 * @since 1.0.0
 */
class ProjectSamplesController {


    private final GetSamplesInfoInput getSamplesInfoInput

    ProjectSamplesController(GetSamplesInfoInput getSamplesInfoInput) {
        this.getSamplesInfoInput = getSamplesInfoInput
    }

    /**
     * Triggers the loading of samples for the given project code
     * @param projectCode identifies the project, samples are loaded for
     * @since 1.0.0
     */
    void getSamplesFor(String projectCode){
        getSamplesInfoInput.requestSampleInfosFor(projectCode)
    }
}
