package life.qbic.portal.sampletracking.components.projectoverview

import com.vaadin.data.provider.ListDataProvider
import com.vaadin.shared.ui.ContentMode
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
    Button postmanLink
    Button downloadData
    private TextArea manifestArea

    final static int MAX_CODE_COLUMN_WIDTH = 400
    final static int MAX_STATUS_COLUMN_WIDTH = 200

    ProjectOverviewView(NotificationService notificationService, ProjectOverviewViewModel viewModel, DownloadProjectController downloadProjectController){
        this.notificationService = notificationService
        this.viewModel = viewModel
        this.downloadProjectController = downloadProjectController

        initLayout()
        setupListeners()
    }

    private void initLayout(){
        titleLabel = new Label("Project Overview")
        titleLabel.addStyleName(ValoTheme.LABEL_LARGE)

        setupProjects()
        manifestArea = setupManifestContent()
        Component component = setupProjectSpecificButtons()


        //todo add download button next to postman link
        this.addComponents(titleLabel,component, projectGrid, manifestArea)
    }

    private void setupProjects(){
        projectGrid = new Grid<>()
        fillProjectsGrid()
        projectGrid.setSelectionMode(Grid.SelectionMode.SINGLE)
    }

    private void selectProject(ProjectSummary projectSummary) {
        viewModel.selectedProject = projectSummary
    }

    private Button setUpLinkButton(){
        Button button = new Button()
        button.setIcon(VaadinIcons.QUESTION_CIRCLE)
        button.setStyleName(ValoTheme.BUTTON_ICON_ONLY + " " + ValoTheme.BUTTON_SMALL + " square")
        button.setDescription("A manifest is a text file passed to download clients to download selected files of interest. <br>" +
                "Use <a href=\"https://github.com/qbicsoftware/postman-cli\" target=\"_blank\">qpostman</a> to download the project data", ContentMode.HTML)
        return button
    }

    private void setupListeners(){
        postmanLink.addClickListener({
            getUI().getPage().open("https://github.com/qbicsoftware/postman-cli#provide-a-file-with-several-qbic-ids","_blank")
        })

        downloadData.addClickListener({
            try {
                tryToDownloadManifest()
            } catch (IllegalArgumentException illegalArgument) {
                notificationService.publishFailure("Manifest Download failed due to: ${illegalArgument.getMessage()}")
            }
        })

        viewModel.addPropertyChangeListener("generatedManifest", { enableIfDownloadIsPossible(downloadData) })

        viewModel.addPropertyChangeListener("selectedProject", { enableIfDownloadIsPossible(downloadData) })

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
        setVisibleIfDownloadIsAvailable(textArea)
        viewModel.addPropertyChangeListener("generatedManifest", {
            textArea.setValue(Optional.ofNullable(it.newValue).orElse("") as String)
            setVisibleIfDownloadIsAvailable(textArea)
        })
        return textArea
    }

    private AbstractComponent setupProjectSpecificButtons() {
        HorizontalLayout buttonBar = new HorizontalLayout()
        buttonBar.setMargin(false)

        postmanLink = setUpLinkButton()
        downloadData = new Button("Download Manifest", VaadinIcons.DOWNLOAD)

        enableIfDownloadIsPossible(downloadData)
        buttonBar.addComponents(downloadData,postmanLink)
        buttonBar.setComponentAlignment(postmanLink, Alignment.MIDDLE_CENTER)

        return buttonBar
    }

    private void tryToDownloadManifest() throws IllegalArgumentException{
        String projectCode = null
        Optional.ofNullable(viewModel.selectedProject).ifPresent({
            projectCode = it.getCode()
        })
        downloadProjectController.downloadProject(projectCode)
    }

    private void enableIfDownloadIsPossible(Component component) {
        component.enabled = viewModel.selectedProject
    }

    private void setVisibleIfDownloadIsAvailable(Component component) {
        component.visible = viewModel.generatedManifest
    }

}
