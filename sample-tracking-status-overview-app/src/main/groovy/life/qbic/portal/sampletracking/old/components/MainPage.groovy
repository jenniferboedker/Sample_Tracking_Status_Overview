package life.qbic.portal.sampletracking.old.components

import com.vaadin.ui.VerticalLayout
import life.qbic.portal.sampletracking.old.components.projectoverview.ProjectView
import life.qbic.portal.sampletracking.old.components.sampleoverview.SampleOverviewController
import life.qbic.portal.sampletracking.old.components.sampleoverview.SampleView

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

        this.setMargin(false)

        this.addComponentsAndExpand(projectLayout, sampleLayout)
        sampleLayout.setVisible(false)
        listenToProjectSelectionChange()
        makeMainPageScrollable()
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

    private void makeMainPageScrollable () {
        this.setWidth("100%");
        this.setHeight("100%");
        this.addStyleName("scrollable-layout")
    }
}
