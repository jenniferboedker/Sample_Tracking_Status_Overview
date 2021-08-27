package life.qbic.portal.sampletracking.components.projectoverview

import com.vaadin.data.provider.ListDataProvider
import com.vaadin.event.selection.SingleSelectionEvent
import com.vaadin.icons.VaadinIcons
import com.vaadin.server.FileDownloader
import com.vaadin.server.StreamResource
import com.vaadin.shared.ui.grid.HeightMode
import com.vaadin.ui.*
import com.vaadin.ui.themes.ValoTheme
import life.qbic.portal.sampletracking.Constants
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

    final static int MAX_CODE_COLUMN_WIDTH = 400
    final static int MAX_STATUS_COLUMN_WIDTH = 200
    private FileDownloader fileDownloader

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
        this.addComponents(titleLabel,setupProjectSpecificButtons(), projectGrid)
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
        viewModel.addPropertyChangeListener("selectedProject", {
            Optional<ProjectSummary> modelSelection = Optional.ofNullable(viewModel.selectedProject)
            Optional<ProjectSummary> viewSelection = projectGrid.getSelectionModel().getFirstSelectedItem()
            boolean viewModelHoldsSelection = modelSelection.isPresent()
            boolean viewSelectionEqualsModelSelection
            if (viewSelection.isPresent() && modelSelection.isPresent()) {
                viewSelectionEqualsModelSelection = viewSelection.get() == modelSelection.get()
            } else {
                viewSelectionEqualsModelSelection = false
            }

            if (viewModelHoldsSelection && !viewSelectionEqualsModelSelection) {
                projectGrid.select(viewModel.selectedProject)
            } else {
                projectGrid.deselectAll()
            }
        })
    }

    private void selectProject(ProjectSummary projectSummary) {
        viewModel.selectedProject = projectSummary
        tryToDownloadManifest()
    }

    private void clearProjectSelection() {
        viewModel.selectedProject = null
        viewModel.generatedManifest = null
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
        projectGrid.addColumn({it.sampleDataAvailable})
                .setCaption("Data Available").setId("SampleDataAvailable")
        setupDataProvider()
        //specify size of grid and layout
        projectGrid.setWidthFull()
        projectGrid.getColumn("ProjectTitle")
                .setMinimumWidth(200)
        projectGrid.getColumn("SamplesReceived")
                .setMaximumWidth(MAX_STATUS_COLUMN_WIDTH).setExpandRatio(1)
        projectGrid.getColumn("SamplesFailedQc")
                .setMaximumWidth(MAX_STATUS_COLUMN_WIDTH).setExpandRatio(1)
        projectGrid.getColumn("SampleDataAvailable")
                .setMaximumWidth(MAX_STATUS_COLUMN_WIDTH).setExpandRatio(1)
        projectGrid.setHeightMode(HeightMode.ROW)
    }

    private void setupDataProvider() {
        def dataProvider = new ListDataProvider(viewModel.projectOverviews)
        projectGrid.setDataProvider(dataProvider)
    }

    private AbstractComponent setupProjectSpecificButtons() {
        VerticalLayout buttonBar = new VerticalLayout()
        buttonBar.setMargin(false)
        Button downloadManifestAction = new Button("Download Manifest", VaadinIcons.DOWNLOAD)
        viewModel.addPropertyChangeListener("generatedManifest", {
            if (isDownloadAvailable()) {
                this.fileDownloader = new FileDownloader(new StreamResource({viewModel.getManifestInputStream()}, "manifest.txt"))
                this.fileDownloader.extend(downloadManifestAction)
            } else {
                if (this.fileDownloader) {
                    if (downloadManifestAction.extensions.contains(fileDownloader)) {
                        downloadManifestAction.removeExtension(this.fileDownloader)
                    }
                }
            }
        })
        enableWhenDownloadIsAvailable(downloadManifestAction)
        buttonBar.addComponent(downloadManifestAction)
        return buttonBar
    }

    private void tryToDownloadManifest() {
        try {
            Optional.ofNullable(viewModel.selectedProject).filter({it.sampleDataAvailable > 0 }).ifPresent({
                String projectCode = it.getCode()
                downloadProjectController.downloadProject(projectCode)
            })
        } catch (IllegalArgumentException illegalArgument) {
            notificationService.publishFailure("Manifest Download failed due to: ${illegalArgument.getMessage()}")
        } catch (Exception ignored) {
            notificationService.publishFailure("Manifest Download failed for unknown reasons. ${Constants.CONTACT_HELPDESK}")
        }
    }

    private void enableWhenDownloadIsAvailable(Component component) {
        component.enabled = isDownloadAvailable()
        viewModel.addPropertyChangeListener("generatedManifest") {
            component.enabled = isDownloadAvailable()
        }
    }

    private boolean isDownloadAvailable() {
        boolean isAvailable = viewModel.generatedManifest as boolean
        return isAvailable
    }

}
