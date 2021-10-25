package life.qbic.portal.sampletracking.components.projectoverview

import life.qbic.business.OutputException
import life.qbic.business.samples.count.CountSamplesOutput
import life.qbic.business.samples.count.StatusCount
import life.qbic.portal.sampletracking.communication.notification.NotificationService
import life.qbic.portal.sampletracking.resource.ResourceService

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
        try {
            notificationService.publishFailure("Failed to count samples: $reason")
        } catch (Exception e) {
            throw new OutputException(e.getMessage())
        }
    }

    /**
     * To be called after successfully counting samples for each status for the provided project code.
     * @param statusCount the count of received statuses
     */
    @Override
    void countedSamples(StatusCount statusCount) {
        statusCountResourceService.addToResource(statusCount)
    }
}
