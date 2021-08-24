package life.qbic.portal.sampletracking.components.projectoverview

import com.vaadin.data.provider.ListDataProvider
import com.vaadin.event.selection.SingleSelectionEvent
import com.vaadin.shared.ui.grid.HeightMode
import com.vaadin.ui.*
import com.vaadin.ui.themes.ValoTheme
import life.qbic.portal.sampletracking.components.projectoverview.download.DownloadProjectController

/**
 * <b>This class generates the layout for the ProductOverview use case</b>
 *
 * <p>This view will be the entry point for the user and provides an overview of her projects. And the projects overall status.
 * From here, the user can navigate to the StatusOverview.</p>
 *
 * @since 1.0.0
 *
*/
class ProjectOverviewView extends VerticalLayout{

    private final ProjectOverviewViewModel viewModel
    private final DownloadProjectController downloadProjectController

    private Label titleLabel
    private Grid<ProjectSummary> projectGrid

    final static int MAX_CODE_COLUMN_WIDTH = 400
    final static int MAX_STATUS_COLUMN_WIDTH = 200

    ProjectOverviewView(ProjectOverviewViewModel viewModel, DownloadProjectController downloadProjectController){
        this.viewModel = viewModel
        this.downloadProjectController = downloadProjectController

        initLayout()
    }

    private void initLayout(){
        titleLabel = new Label("Project Overview")
        titleLabel.addStyleName(ValoTheme.LABEL_LARGE)
        setupProjects()
        this.addComponent(setupProjectSpecificButtons())
        this.addComponents(titleLabel, projectGrid)
    }

    private void setupProjects(){
        projectGrid = new Grid<>()
        fillProjectsGrid()
        projectGrid.setSelectionMode(Grid.SelectionMode.SINGLE)
        projectGrid.addSelectionListener({
            if (it instanceof SingleSelectionEvent<ProjectSummary>) {
                viewModel.selectedProject = it.getSelectedItem().orElse(null)
            }
        })
        viewModel.addPropertyChangeListener("selectedProjectCode", {
            projectGrid.select(viewModel.selectedProject)
        })
    }

    private void fillProjectsGrid() {
        projectGrid.addColumn({ it.code})
                .setCaption("Project Code").setId("ProjectCode").setMaximumWidth(MAX_CODE_COLUMN_WIDTH)
        projectGrid.addColumn({ it.title })
                .setCaption("Project Title").setId("ProjectTitle")
        projectGrid.addColumn({it.samplesReceived})
                .setCaption("Samples Received").setId("SamplesReceived")
        projectGrid.addColumn({it.samplesQcFailed})
                .setCaption("Samples Failed QC").setId("SamplesFailedQc")
        setupDataProvider()
        //specify size of grid and layout
        projectGrid.setWidthFull()
        projectGrid.getColumn("ProjectTitle")
                .setMinimumWidth(200)
        projectGrid.getColumn("SamplesReceived")
                .setMaximumWidth(MAX_STATUS_COLUMN_WIDTH).setExpandRatio(1)
        projectGrid.getColumn("SamplesFailedQc")
                .setMaximumWidth(MAX_STATUS_COLUMN_WIDTH).setExpandRatio(1)
        projectGrid.setHeightMode(HeightMode.ROW)
    }

    private void setupDataProvider() {
        def dataProvider = new ListDataProvider(viewModel.projectOverviews)
        projectGrid.setDataProvider(dataProvider)
    }

    private AbstractComponent setupProjectSpecificButtons() {
        MenuBar buttonBar = new MenuBar()
        buttonBar.addItem("Download Project", {
            assert viewModel.selectedProject
            downloadProjectController.downloadProject(Optional.of(viewModel.selectedProject)
                    .map({it.code}).get())
        })
        return buttonBar
    }
}
