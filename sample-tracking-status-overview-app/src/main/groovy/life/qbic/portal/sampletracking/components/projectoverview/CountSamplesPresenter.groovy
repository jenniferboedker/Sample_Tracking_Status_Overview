package life.qbic.portal.sampletracking.components.projectoverview

import life.qbic.business.samples.count.CountSamplesOutput
import life.qbic.datamodel.samples.Status
import life.qbic.portal.sampletracking.communication.notification.NotificationService
import life.qbic.portal.sampletracking.resource.ResourceService
import life.qbic.portal.sampletracking.resource.status.StatusCount

/**
 * <p>Implements {@link CountSamplesOutput} and presents to the application</p>
 *
 * @since 1.0.0
 */
class CountSamplesPresenter implements CountSamplesOutput {

    private final NotificationService notificationService
    private final ResourceService<StatusCount> statusCountResourceService

    CountSamplesPresenter(NotificationService notificationService, ResourceService<StatusCount> statusCountResourceService) {
        this.notificationService = notificationService
        this.statusCountResourceService = statusCountResourceService
    }

    /**
     * To be called when the execution of the samples counting failed
     * @param reason the reason why the samples retrieval failed
     * @since 1.0.0
     */
    @Override
    void failedExecution(String reason) {
        notificationService.publishFailure("Failed to count samples: $reason")
    }

    /**
     * To be called after successfully counting samples for the provided code.
     * @param number of all samples and samples that have been received by the lab
     * @since 1.0.0
     */
    @Override
    void countedReceivedSamples(String projectCode, int allSamples, int receivedSamples) {
        StatusCount statusCount = new StatusCount(projectCode, Status.SAMPLE_RECEIVED, receivedSamples)
        statusCountResourceService.addToResource(statusCount)
    }

    @Override
    void countedFailedQcSamples(String projectCode, int allSamples, int receivedSamples) {
        StatusCount statusCount = new StatusCount(projectCode, Status.SAMPLE_QC_FAIL, receivedSamples)
        statusCountResourceService.addToResource(statusCount)
    }

    @Override
    void countedAvailableSampleData(String projectCode, int allSamples, int availableData) {

    }
}
