package life.qbic.portal.sampletracking.components


import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.themes.ValoTheme
import life.qbic.portal.sampletracking.components.projectoverview.ProjectOverviewView
import life.qbic.portal.sampletracking.components.sampleoverview.SampleOverviewView
import life.qbic.portal.sampletracking.components.toggle.ToggleButton

/**
 * <b><short description></b>
 *
 * <p><detailed description></p>
 *
 * @since <version tag>
 */
class MainView extends VerticalLayout {

    private final ProjectOverviewView projectOverviewView
    private final SampleOverviewView sampleOverviewView
    private final HorizontalLayout hotbar = new HorizontalLayout()
    protected final Label titleLabel = new Label("Sample Status Portlet")
    private final ToggleButton projectSampleToggle

    MainView(ProjectOverviewView projectOverviewView, SampleOverviewView sampleOverviewView) {
        this.setMargin(true)
        this.setSpacing(false)

        titleLabel.addStyleName(ValoTheme.LABEL_HUGE)

        this.projectOverviewView = projectOverviewView
        this.sampleOverviewView = sampleOverviewView

        projectSampleToggle = setupProjectSampleToggle()
        addToggleButtonListeners()

        hotbar.addComponentAsFirst(projectSampleToggle)
        addHotbarItem(projectOverviewView.getHotbar())
        addHotbarItem(sampleOverviewView.getHotbar())
        showProjectView(true)
        showSampleView(false)

        this.addComponents(titleLabel, hotbar, projectOverviewView, sampleOverviewView)
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

    private static ToggleButton setupProjectSampleToggle() {
        ToggleButton toggleButton = new ToggleButton("Sample View", "Project View")
        return toggleButton
    }
}
