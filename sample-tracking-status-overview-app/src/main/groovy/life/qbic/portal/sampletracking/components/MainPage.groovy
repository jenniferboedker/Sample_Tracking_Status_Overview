package life.qbic.portal.sampletracking.components

import com.vaadin.ui.VerticalLayout
import life.qbic.portal.sampletracking.components.projectoverview.ProjectView
import life.qbic.portal.sampletracking.components.sampleoverview.SampleOverviewController
import life.qbic.portal.sampletracking.components.sampleoverview.SampleView

class MainPage extends VerticalLayout {

    private final ProjectView projectView
    private final SampleView sampleView
    private final ViewModel viewModel
    private final SampleOverviewController controller

    MainPage(ProjectView projectLayout, SampleView sampleLayout, ViewModel viewModel, SampleOverviewController sampleOverviewController) {
        this.projectView = projectLayout
        this.sampleView = sampleLayout
        this.viewModel = viewModel
        this.controller = sampleOverviewController

        this.addComponents(projectLayout, sampleLayout)
        sampleLayout.setVisible(false)

        listenToProjectSelectionChange()
    }

    private void listenToProjectSelectionChange() {
        projectView.onSelectedProjectChange({
            if (it) {
                controller.getSamplesFor(it.code)
            } else {
                sampleView.reset()
            }
        })

        viewModel.addPropertyChangeListener("projectViewEnabled", {
            projectView.setVisible(viewModel.projectViewEnabled)
            sampleView.setVisible(!viewModel.projectViewEnabled)
        })
    }
}
