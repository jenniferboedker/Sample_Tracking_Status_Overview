package life.qbic.portal.sampletracking.components.projectoverview.samplelist

import life.qbic.business.samples.info.GetSamplesInfoInput
import life.qbic.datamodel.samples.Status

/**
 * <b><short description></b>
 *
 * <p><detailed description></p>
 *
 * @since 1.0.0
 */
class ProjectOverviewController {

    private GetSamplesInfoInput getSamplesInfoInput

    ProjectOverviewController(GetSamplesInfoInput samplesInfoInput){
        this.getSamplesInfoInput = samplesInfoInput
    }

    void getFailedQcSamples(String projectCode){
        getSamplesInfoInput.requestSampleInfosFor(projectCode, Status.SAMPLE_QC_FAIL)
    }

}