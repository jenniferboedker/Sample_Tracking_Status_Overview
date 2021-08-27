package life.qbic.portal.sampletracking.components.projectoverview.download

import life.qbic.business.OutputException
import life.qbic.business.samples.download.DownloadSamplesOutput
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
class ManifestPresenter implements DownloadSamplesOutput {

    private final ProjectOverviewViewModel viewModel
    private final NotificationService notificationService

    ManifestPresenter(NotificationService notificationService, ProjectOverviewViewModel viewModel) {
        this.notificationService = notificationService
        this.viewModel = viewModel
    }

    @Override
    void failedExecution(String reason) {
        try {
            sendFailure("Could not create download manifest. Reason: $reason")
        } catch (Exception e) {
            throw new OutputException(e.getMessage())
        }
    }

    private void sendFailure(String message) {
        notificationService.publishFailure("Could not create download manifest. Reason: $reason")
    }

    /**
     * To be called after successfully fetching sample codes with data for the provided code.
     * @param projectCode the code of the project samples were counted for
     * @param sampleCodes list of sample codes with available data
     * @since 1.0.0
     */
    @Override
    void foundDownloadableSamples(String projectCode, List<String> sampleCodes) throws OutputException {
        try {
            if (sampleCodes.empty) {
                sendFailure("There are no downloadable samples for ${projectCode}.")
                return
            }
            DownloadManifest downloadManifest = DownloadManifest.from(sampleCodes)
            String printableManifest = DownloadManifestFormatter.format(downloadManifest)
            viewModel.generatedManifest = Optional.ofNullable(printableManifest).orElse("")
        } catch (Exception ignored) {
            throw new OutputException("Could not generate manifest for samples: ${sampleCodes.toListString()}")
        }
    }
}
