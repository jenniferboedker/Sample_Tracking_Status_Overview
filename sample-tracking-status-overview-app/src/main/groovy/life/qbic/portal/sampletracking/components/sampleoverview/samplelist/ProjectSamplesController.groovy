package life.qbic.portal.sampletracking.components.sampleoverview.samplelist

import life.qbic.business.samples.info.GetSamplesInfoInput

/**
 * <b><short description></b>
 *
 * <p><detailed description></p>
 *
 * @since <version tag>
 */
class ProjectSamplesController {


    private final GetSamplesInfoInput getSamplesInfoInput

    ProjectSamplesController(GetSamplesInfoInput getSamplesInfoInput) {
        this.getSamplesInfoInput = getSamplesInfoInput
    }

    void getSamplesFor(String projectCode){
        getSamplesInfoInput.requestSampleInfosFor(projectCode)
    }
}
