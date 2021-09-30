package life.qbic.portal.sampletracking.components.projectoverview

import com.vaadin.data.ValueProvider
import com.vaadin.data.provider.ListDataProvider
import com.vaadin.event.selection.SingleSelectionEvent
import com.vaadin.icons.VaadinIcons
import com.vaadin.server.FileDownloader
import com.vaadin.server.StreamResource
import com.vaadin.shared.ui.ContentMode
import com.vaadin.shared.ui.grid.HeightMode
import com.vaadin.ui.*
import com.vaadin.ui.themes.ValoTheme
import life.qbic.portal.sampletracking.Constants
import life.qbic.portal.sampletracking.communication.notification.NotificationService
import life.qbic.portal.sampletracking.components.projectoverview.download.DownloadProjectController
import life.qbic.portal.sampletracking.components.projectoverview.samplelist.FailedQCSamplesView
import life.qbic.portal.sampletracking.components.projectoverview.samplelist.ProjectOverviewController
import life.qbic.portal.sampletracking.components.projectoverview.statusdisplay.RelativeCount
import life.qbic.portal.sampletracking.components.projectoverview.statusdisplay.State
import life.qbic.portal.sampletracking.components.projectoverview.subscribe.SubscribeProjectController

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
    private final SubscribeProjectController subscribeProjectController
    private final NotificationService notificationService
    private final FailedQCSamplesView failedQCSamplesView
    private final ProjectOverviewController projectOverviewController

    private Grid<ProjectSummary> projectGrid

    final static int MAX_CODE_COLUMN_WIDTH = 400
    final static int MAX_STATUS_COLUMN_WIDTH = 200
    private FileDownloader fileDownloader

    ProjectOverviewView(NotificationService notificationService, ProjectOverviewViewModel viewModel, DownloadProjectController downloadProjectController
                        , FailedQCSamplesView failedQCSamplesView, ProjectOverviewController projectOverviewController, SubscribeProjectController subscribeProjectController){
        this.notificationService = notificationService
        this.viewModel = viewModel
        this.downloadProjectController = downloadProjectController
        this.subscribeProjectController = subscribeProjectController
        this.failedQCSamplesView = failedQCSamplesView
        this.projectOverviewController = projectOverviewController

        initLayout()
    }

    private void initLayout(){
        Label titleLabel = new Label("Project Overview")
        titleLabel.addStyleName(ValoTheme.LABEL_LARGE)
        setupProjects()
        HorizontalLayout buttonBar = setupButtonLayout()
        failedQCSamplesView.setVisible(false)

        this.addComponents(titleLabel,buttonBar, projectGrid, failedQCSamplesView)
    }

    private HorizontalLayout setupButtonLayout() {
        HorizontalLayout buttonBar = new HorizontalLayout()
        buttonBar.setMargin(false)

        Button postmanLink = setUpLinkButton()
        Button downloadManifestAction = setupDownloadButton()
        Button showDetails = setupShowDetails()
        CheckBox subscriptionCheckBox = setupSubscriptionCheckBox()
        buttonBar.addComponents(showDetails, downloadManifestAction, postmanLink, subscriptionCheckBox)
        buttonBar.setComponentAlignment(postmanLink, Alignment.MIDDLE_CENTER)
        buttonBar.setComponentAlignment(subscriptionCheckBox, Alignment.MIDDLE_CENTER)
        return buttonBar
    }

    private Button setupShowDetails(){
        Button detailsButton = new Button("Show Details")
        detailsButton.setIcon(VaadinIcons.INFO_CIRCLE)
        detailsButton.setEnabled(false)

        projectGrid.addSelectionListener({
            failedQCSamplesView.setVisible(false)

            if(viewModel.selectedProject && viewModel.selectedProject.samplesQcFailed > 0){
                detailsButton.setEnabled(true)
            }else{
                detailsButton.setEnabled(false)
            }
        })

        detailsButton.addClickListener({
            if(viewModel.selectedProject){
                projectOverviewController.getFailedQcSamples(viewModel.selectedProject.code)
                failedQCSamplesView.setVisible(true)
            }
        })

        return detailsButton
    }

    private Button setUpLinkButton(){
        Button button = new Button()
        button.setIcon(VaadinIcons.QUESTION_CIRCLE)
        button.setStyleName(ValoTheme.BUTTON_ICON_ONLY + " " + ValoTheme.BUTTON_SMALL + " square")
        button.setDescription("A manifest is a text file used by a client application (e.g. <a href=\"https://github.com/qbicsoftware/postman-cli\" target=\"_blank\">qpostman</a>) to download selected files of interest. <br>" +
                "Use <a href=\"https://github.com/qbicsoftware/postman-cli\" target=\"_blank\">qpostman</a> to download the data.", ContentMode.HTML)

        button.addClickListener({
            getUI().getPage().open(
                    "https://github.com/qbicsoftware/postman-cli#provide-a-file-with-several-qbic-ids",
                    "_blank")
        })
        return button
    }

    private Button setupDownloadButton() {
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
        return downloadManifestAction
    }

    private CheckBox setupSubscriptionCheckBox() {

        CheckBox subscriptionCheckBox = new CheckBox("Subscribe")
        subscriptionCheckBox.setVisible(false)
        enableWhenProjectIsSelected(subscriptionCheckBox)
        subscriptionCheckBox.setValue(false)
        subscriptionCheckBox.addValueChangeListener(event -> {
            //Only Subscribe if checkbox is checked
            if (subscriptionCheckBox.value && viewModel.selectedProject) {
                subscribeToProject(viewModel.selectedProject.code)
            }
        })
        return subscriptionCheckBox
    }

    private void setupProjects() {
        projectGrid = new Grid<ProjectSummary>()
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
            modelSelection.ifPresent({
                if (viewSelection.isPresent()) {
                    if (viewSelection.get() == modelSelection.get()) {
                        // do nothing
                    } else {
                        projectGrid.getSelectionModel().deselectAll()
                        projectGrid.select(modelSelection.get())
                    }
                } else {
                    projectGrid.select(modelSelection.get())
                }
            })
            //for each selected
            failedQCSamplesView.setVisible(false)
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

    private static String getStyleForColumn(ProjectSummary projectSummary, ValueProvider<ProjectSummary, RelativeCount> valueProvider) {
        RelativeCount relativeCount = valueProvider.apply(projectSummary)
        State state = determineCompleteness(relativeCount)
        return state.getCssClass()
    }

    private static String getStyleForFailureColumn(ProjectSummary projectSummary, ValueProvider<ProjectSummary, RelativeCount> valueProvider) {
        RelativeCount relativeCount = valueProvider.apply(projectSummary)
        State state = determineFailure(relativeCount)
        return state.getCssClass()
    }


    private void fillProjectsGrid() {

        projectGrid.addColumn({ it.code })
                .setCaption("Project Code").setId("ProjectCode").setMaximumWidth(
                MAX_CODE_COLUMN_WIDTH)
        projectGrid.addColumn({ it.title })
                .setCaption("Project Title").setId("ProjectTitle")

        ValueProvider<ProjectSummary, RelativeCount> receivedProvider = { ProjectSummary it ->
            new RelativeCount(it.samplesReceived.passingSamples, it.totalSampleCount )
        }
        projectGrid.addColumn(receivedProvider).setStyleGenerator({getStyleForColumn(it, receivedProvider)})
                .setCaption("Samples Received").setId("SamplesReceived")

        ValueProvider<ProjectSummary, RelativeCount> failedQcProvider = { ProjectSummary it ->
            new RelativeCount(it.samplesQcPassed.passingSamples, it.totalSampleCount )
        }
        projectGrid.addColumn(failedQcProvider)
                .setCaption("Samples Passed QC").setId("SamplesPassedQc").setStyleGenerator({getStyleForFailureColumn(it, failedQcProvider)})

        ValueProvider<ProjectSummary, RelativeCount> libraryPrepProvider = {ProjectSummary it ->
            new RelativeCount(it.samplesLibraryPrepFinished.passingSamples , it.totalSampleCount)
        }
        projectGrid.addColumn(libraryPrepProvider)
                .setCaption("Library Prep Finished").setId("LibraryPrepFinished").setStyleGenerator({getStyleForColumn(it, libraryPrepProvider)})

        ValueProvider<ProjectSummary, RelativeCount> dataAvailableProvider = { ProjectSummary it ->
            new RelativeCount(it.sampleDataAvailable.passingSamples , it.totalSampleCount)
        }
        projectGrid.addColumn(dataAvailableProvider).setStyleGenerator({getStyleForColumn(it, dataAvailableProvider)})
                .setCaption("Data Available").setId("SampleDataAvailable")

        setupDataProvider()
        //specify size of grid and layout
        projectGrid.setWidthFull()
        projectGrid.getColumn("ProjectTitle")
                .setMinimumWidth(200)
        projectGrid.getColumn("SamplesReceived")
                .setMaximumWidth(MAX_STATUS_COLUMN_WIDTH).setExpandRatio(1)
        projectGrid.getColumn("SamplesPassedQc")
                .setMaximumWidth(MAX_STATUS_COLUMN_WIDTH).setExpandRatio(1)
        projectGrid.getColumn("LibraryPrepFinished")
                .setMaximumWidth(MAX_STATUS_COLUMN_WIDTH).setExpandRatio(1)
        projectGrid.getColumn("SampleDataAvailable")
                .setMaximumWidth(MAX_STATUS_COLUMN_WIDTH).setExpandRatio(1)
        projectGrid.setHeightMode(HeightMode.ROW)
    }

    private void setupDataProvider() {
        def dataProvider = new ListDataProvider(viewModel.projectOverviews)
        projectGrid.setDataProvider(dataProvider)
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

    private void enableWhenProjectIsSelected(CheckBox checkBox) {
        viewModel.addPropertyChangeListener("selectedProject") {
            checkBox.setValue(false)
            if(viewModel.selectedProject){
             checkBox.setVisible(true)
            }else{
              checkBox.setVisible(false)
            }
        }
    }

    private void subscribeToProject(String projectCode) {
        if (viewModel.subscriber) {
            if (projectCode) {
                subscribeProjectController.subscribeProject(viewModel.subscriber, projectCode)
            }
        }
    }

    /**
     * Determines the state of the current status. Is it in progress or did it complete already
     * @param samplesInStatus the count for the specific status
     * @param relativeCount
     */
    private static State determineCompleteness(RelativeCount relativeCount) {
        int samplesInStatus = relativeCount.getValue()
        int totalSamples = relativeCount.getTotal()
        if (samplesInStatus == totalSamples) {
            return State.COMPLETED
        } else if (samplesInStatus < totalSamples) {
            return State.IN_PROGRESS
        } else {
            //unexpected!!
            throw new IllegalStateException("status count $samplesInStatus must not be greater total count $totalSamples")
        }
    }

    /**
     * Determines the state of the current status. Is it in progress or were failures observed.
     * @param relativeCount
     * @return the state of the project for the status in question
     */
    private static State determineFailure(RelativeCount relativeCount) {
        if (relativeCount.value > 0) {
            return State.FAILED
        } else {
            return State.IN_PROGRESS
        }
    }
}
