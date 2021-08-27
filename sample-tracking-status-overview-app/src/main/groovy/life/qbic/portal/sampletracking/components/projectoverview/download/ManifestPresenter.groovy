package life.qbic.portal.sampletracking.components.projectoverview.download

import life.qbic.business.OutputException
import life.qbic.portal.sampletracking.communication.notification.NotificationService
import life.qbic.portal.sampletracking.components.projectoverview.ProjectOverviewViewModel

/**
 * <b>Presents a download manifest to the viewModel containing identifiers that point to associated data</b>
 *
 * <p>Is called from a use case with a manifest String containing identifiers (e.g. of samples) that have associated data.
 * This manifest is added to the view model in order to further use it, e.g. present it to the user.</p>
 *
 * @since 1.0.0
 */
class ManifestPresenter implements ComposeManifestOutput {

    private final ProjectOverviewViewModel viewModel
    private final NotificationService notificationService

    ManifestPresenter(NotificationService notificationService, ProjectOverviewViewModel viewModel) {
        this.notificationService = notificationService
        this.viewModel = viewModel
    }

    @Override
    void readManifest(String printedManifest) {
        try {
            String newValue = Optional.ofNullable(printedManifest).orElse("")
            viewModel.setGeneratedManifest(newValue)
        } catch (Exception e) {
            throw new OutputException(e.getMessage())
        }
    }

    @Override
    void failedExecution(String reason) {
        try {
            notificationService.publishFailure("Could not create download manifest. Reason: $reason")
        } catch (Exception e) {
            throw new OutputException(e.getMessage())
        }
    }
}
