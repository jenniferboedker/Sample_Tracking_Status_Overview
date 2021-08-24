package life.qbic.portal.sampletracking.components.projectoverview.download

import life.qbic.business.samples.download.ComposeManifestOutput
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

    ManifestPresenter(ProjectOverviewViewModel viewModel) {
        this.viewModel = viewModel
    }

    @Override
    void readManifest(String printedManifest) {
        //TODO implement
    }

    @Override
    void failedExecution(String reason) {
        //TODO implement
    }
}
