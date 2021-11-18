package life.qbic.portal.sampletracking.components

import com.vaadin.ui.Component
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.themes.ValoTheme
import life.qbic.portal.sampletracking.components.projectoverview.ProjectOverviewView
import life.qbic.portal.sampletracking.components.projectoverview.ProjectOverviewViewModel
import life.qbic.portal.sampletracking.components.sampleoverview.SampleOverviewView
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
    private final HorizontalLayout hotbar = new HorizontalLayout()
    protected final Label titleLabel = new Label("Sample Status Portlet")
    private final ToggleButton projectSampleToggle

    AppView(ProjectOverviewView projectOverviewView, SampleOverviewView sampleOverviewView) {
        this.setMargin(true)
        this.setSpacing(true)

        titleLabel.addStyleName(ValoTheme.LABEL_HUGE)

        this.projectOverviewView = projectOverviewView
        this.sampleOverviewView = sampleOverviewView

        projectSampleToggle = setupProjectSampleToggle()

        addToggleButtonListeners()
        addProjectSelectionListener()

        hotbar.addComponentAsFirst(projectSampleToggle)
        addHotbarItem(projectOverviewView.getHotbar())
        addHotbarItem(sampleOverviewView.getHotbar())
        showProjectView(true)
        showSampleView(false)

        this.addComponents(titleLabel, createSpacer(2, Unit.EM), hotbar, projectOverviewView, sampleOverviewView)
    }

    private void addProjectSelectionListener(){
        projectOverviewView.onSelectedProject({
            if(it){
                projectSampleToggle.setEnabled(true)
                sampleOverviewView.loadProjectSamples(it.code)
            }else{
                projectSampleToggle.setEnabled(false)
                sampleOverviewView.resetContent()
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
