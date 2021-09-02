package life.qbic.portal.sampletracking.components.projectoverview

import life.qbic.business.OutputException
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
        try {
            notificationService.publishFailure("Failed to count samples: $reason")
        } catch (Exception e) {
            throw new OutputException(e.getMessage())
        }
    }

    /**
     * To be called after successfully counting samples for the provided code.
     * @param number of all samples and samples that have been received by the lab
     * @since 1.0.0
     */
    @Override
    void countedReceivedSamples(String projectCode, int allSamples, int receivedSamples) {
        try {
            StatusCount statusCount = new StatusCount(projectCode, Status.SAMPLE_RECEIVED, receivedSamples)
            statusCountResourceService.addToResource(statusCount)
        } catch (Exception e) {
            throw new OutputException(e.getMessage())
        }
    }

    @Override
    void countedFailedQcSamples(String projectCode, int allSamples, int receivedSamples) {
        try {
            StatusCount statusCount = new StatusCount(projectCode, Status.SAMPLE_QC_FAIL, receivedSamples)
            statusCountResourceService.addToResource(statusCount)
        } catch (Exception e) {
            throw new OutputException(e.getMessage())
        }
    }

    @Override
    void countedAvailableSampleData(String projectCode, int allSamples, int availableData) {
        StatusCount statusCount = new StatusCount(projectCode, Status.DATA_AVAILABLE, availableData)
        statusCountResourceService.addToResource(statusCount)
    }

    /**
     * To be called after successfully counting samples with finished library prep for the provided code.
     * @param number of all samples and samples that have available data
     * @param projectCode the code of the project samples were counted for
     * @since 1.0.0
     */
    @Override
    void countedLibraryPrepFinishedSamples(String projectCode, int allSamples, int availableData) {
        StatusCount statusCount = new StatusCount(projectCode, Status.LIBRARY_PREP_FINISHED, availableData)
        statusCountResourceService.addToResource(statusCount)
    }
}
