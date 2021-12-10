package life.qbic.portal.sampletracking.components

import com.vaadin.ui.Button
import com.vaadin.ui.Component
import com.vaadin.ui.Grid
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.components.grid.ItemClickListener
import com.vaadin.ui.themes.ValoTheme
import life.qbic.portal.sampletracking.components.projectoverview.ProjectOverviewView
import life.qbic.portal.sampletracking.components.projectoverview.ProjectSummary
import life.qbic.portal.sampletracking.components.projectoverview.Projectview
import life.qbic.portal.sampletracking.components.sampleoverview.SampleOverviewController
import life.qbic.portal.sampletracking.components.sampleoverview.SampleOverviewView
import life.qbic.portal.sampletracking.components.sampleoverview.SampleView
import life.qbic.portal.sampletracking.components.toggle.ToggleButton

/**
 * <b>The application view component</b>
 *
 * <p>This is the orchestrating root element of the view hierarchy.</p>
 *
 * @since 1.0.0
 */
class AppView extends VerticalLayout {

    private final ProjectOverviewView projectOverviewView
    private final SampleOverviewView sampleOverviewView
    private final SampleOverviewController sampleOverviewController

    private final HorizontalLayout hotbar = new HorizontalLayout()
    protected final Label titleLabel = new Label("Sample Status Portlet")
    private final ToggleButton projectSampleToggle

    AppView(ProjectOverviewView projectOverviewView, SampleOverviewView sampleOverviewView, SampleOverviewController sampleOverviewController) {
        this.setMargin(true)
        this.setSpacing(true)

        titleLabel.addStyleName(ValoTheme.LABEL_HUGE)

        this.projectOverviewView = projectOverviewView
        this.sampleOverviewView = sampleOverviewView
        this.sampleOverviewController = sampleOverviewController

        projectSampleToggle = setupProjectSampleToggle()

        addToggleButtonListeners()
        listenToProjectSelectionChange()

        hotbar.addComponentAsFirst(projectSampleToggle)
        addHotbarItem(projectOverviewView.getHotbar())
        addHotbarItem(sampleOverviewView.getHotbar())
        showProjectView(true)
        showSampleView(false)

        this.addComponents(titleLabel, createSpacer(2, Unit.EM), hotbar, new Projectview(), new SampleView())
    }


    private void listenToProjectSelectionChange(){
        projectOverviewView.onSelectedProjectChange({
            if (it) {
                projectSampleToggle.setEnabled(true)
                sampleOverviewController.getSamplesFor(it.code)
            } else {
                projectSampleToggle.setEnabled(false)
                sampleOverviewView.reset()
            }
        })
    }

    private void showProjectView(Boolean visible) {
        projectOverviewView.setVisible(visible)
        projectOverviewView.getHotbar().setVisible(visible)
        if (visible) this.titleLabel.value = projectOverviewView.getTitle()
    }

    private void showSampleView(Boolean visible) {
        sampleOverviewView.setVisible(visible)
        sampleOverviewView.getHotbar().setVisible(visible)
        if (visible) this.titleLabel.value = sampleOverviewView.getTitle()
    }

    private void addHotbarItem(HorizontalLayout item) {
        this.hotbar.addComponent(item)
    }


    private void addToggleButtonListeners() {
        def switchToSampleView = {
            showProjectView(false)
            showSampleView(true)
        }
        def switchToProjectView = {
            showSampleView(false)
            showProjectView(true)
        }
        projectSampleToggle.addClickListener(switchToSampleView, ToggleButton.State.ONE)
        projectSampleToggle.addClickListener(switchToProjectView, ToggleButton.State.TWO)

        projectOverviewView.onProjectDoubleClick({
            sampleOverviewController.getSamplesFor(it.code)
            projectSampleToggle.click()
        })
    }

    private static Component createSpacer(float height, Unit unit) {
        Label label = new Label(" ")
        label.setHeight(height, unit)
        return label
    }

    private static ToggleButton setupProjectSampleToggle() {
        ToggleButton toggleButton = new ToggleButton("Show Samples", "Show Projects")
        toggleButton.setEnabled(false)

        return toggleButton
    }
}
