package life.qbic.portal.sampletracking.components.projectoverview.download

import life.qbic.business.samples.download.ComposeManifestOutput
import life.qbic.portal.sampletracking.communication.notification.NotificationService
import life.qbic.portal.sampletracking.components.projectoverview.ProjectOverviewViewModel

/**
 * <b><short description></b>
 *
 * <p><detailed description></p>
 *
 * @since <version tag>
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
        viewModel.generatedManifest = Optional.ofNullable(printedManifest).orElse("")
    }

    @Override
    void failedExecution(String reason) {
        notificationService.publishFailure("Could not create download manifest. Reason: $reason")
    }
}
