package life.qbic.portal.sampletracking.components.projectoverview.samplelist

import life.qbic.business.samples.info.GetSamplesInfoInput
import life.qbic.datamodel.samples.Status

/**
 * <b>Controls the dataflow into the GetSamplesInfo use case.</b>
 *
 * @since 1.0.0
 */
class FailedQCSamplesController {

    private GetSamplesInfoInput getSamplesInfoInput

    FailedQCSamplesController(GetSamplesInfoInput samplesInfoInput){
        this.getSamplesInfoInput = samplesInfoInput
    }

    void getFailedQcSamples(String projectCode){
        getSamplesInfoInput.requestSampleInfosFor(projectCode, Status.SAMPLE_QC_FAIL)
    }

}