package life.qbic.portal.sampletracking.components.sampleoverview

import com.vaadin.ui.Button
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.VerticalLayout
import life.qbic.portal.sampletracking.components.HasHotbar
import life.qbic.portal.sampletracking.components.HasTitle
import life.qbic.portal.sampletracking.components.sampleoverview.samplelist.ProjectSamplesController
import life.qbic.portal.sampletracking.components.sampleoverview.samplelist.ProjectSamplesView

/**
 * <b><short description></b>
 *
 * <p><detailed description></p>
 *
 * @since <version tag>
 */
class SampleOverviewView extends VerticalLayout implements HasHotbar, HasTitle {

    private static final String TITLE = "Sample Overview"

    private final ProjectSamplesController projectSamplesController
    private final ProjectSamplesView projectSamplesView

    private final HorizontalLayout hotbar = new HorizontalLayout(new Button("Sample Klick"))

    SampleOverviewView(ProjectSamplesController projectSamplesController, ProjectSamplesView projectSamplesView) {
        this.projectSamplesController = projectSamplesController
        this.projectSamplesView = projectSamplesView
        this.addComponents(this.projectSamplesView)
        this.setMargin(false)
    }

    @Override
    HorizontalLayout getHotbar() {
        return hotbar
    }

    @Override
    String getTitle() {
        return TITLE
    }
}
