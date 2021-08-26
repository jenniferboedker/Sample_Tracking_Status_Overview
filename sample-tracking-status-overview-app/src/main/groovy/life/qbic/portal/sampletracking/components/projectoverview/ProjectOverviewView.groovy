package life.qbic.portal.sampletracking.components.projectoverview

import com.vaadin.data.provider.ListDataProvider
import com.vaadin.event.selection.SingleSelectionEvent
import com.vaadin.icons.VaadinIcons
import com.vaadin.shared.ui.grid.HeightMode
import com.vaadin.ui.*
import com.vaadin.ui.themes.ValoTheme
import life.qbic.portal.sampletracking.communication.notification.NotificationService
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
    private final NotificationService notificationService

    private Label titleLabel
    private Grid<ProjectSummary> projectGrid
    private TextArea manifestArea

    final static int MAX_CODE_COLUMN_WIDTH = 400
    final static int MAX_STATUS_COLUMN_WIDTH = 200

    ProjectOverviewView(NotificationService notificationService, ProjectOverviewViewModel viewModel, DownloadProjectController downloadProjectController){
        this.notificationService = notificationService
        this.viewModel = viewModel
        this.downloadProjectController = downloadProjectController

        initLayout()
    }

    private void initLayout(){
        titleLabel = new Label("Project Overview")
        titleLabel.addStyleName(ValoTheme.LABEL_LARGE)
        setupProjects()
        manifestArea = setupManifestContent()
        this.addComponents(titleLabel,setupProjectSpecificButtons(), projectGrid,manifestArea)
    }

    private void setupProjects(){
        projectGrid = new Grid<>()
        fillProjectsGrid()
        projectGrid.setSelectionMode(Grid.SelectionMode.SINGLE)
        projectGrid.addSelectionListener({
            if (it instanceof SingleSelectionEvent<ProjectSummary>) {
                clearProjectSelection()
                it.getSelectedItem().ifPresent(this::selectProject)
            }
        })
        viewModel.addPropertyChangeListener("selectedProjectCode", {
            projectGrid.select(viewModel.selectedProject)
        })
    }

    private void selectProject(ProjectSummary projectSummary) {
        viewModel.selectedProject = projectSummary
        manifestArea.setVisible(true)
    }

    private void clearProjectSelection() {
        viewModel.selectedProject = null
        manifestArea.setVisible(false)
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

    private TextArea setupManifestContent() {
        TextArea textArea = new TextArea("Download Manifest")
        textArea.setReadOnly(true)
        textArea.setVisible(false)
        viewModel.addPropertyChangeListener("generatedManifest", {
            textArea.setValue(Optional.ofNullable(it.newValue).orElse("") as String)
        })
        return textArea
    }

    private AbstractComponent setupProjectSpecificButtons() {
        VerticalLayout buttonBar = new VerticalLayout()
        Button downloadManifestAction = new Button("Download Manifest", VaadinIcons.DOWNLOAD)
        downloadManifestAction.addClickListener({
            try {
                tryToDownloadManifest()
            } catch (IllegalArgumentException illegalArgument) {
                notificationService.publishFailure("Manifest Download failed due to: ${illegalArgument.getMessage()}")
            }
        })
        viewModel.addPropertyChangeListener("generatedManifest", { enableIfDownloadIsPossible(downloadManifestAction) })
        viewModel.addPropertyChangeListener("selectedProject", { enableIfDownloadIsPossible(downloadManifestAction) })
        enableIfDownloadIsPossible(downloadManifestAction)
        buttonBar.addComponent(downloadManifestAction)
        return buttonBar
    }

    private void tryToDownloadManifest() {
        String projectCode = null
        Optional.ofNullable(viewModel.selectedProject).ifPresent({
            projectCode = it.getCode()
        })
        downloadProjectController.downloadProject(projectCode)
    }

    private boolean enableIfDownloadIsPossible(Component component) {
        component.enabled = viewModel.selectedProject
    }
}
