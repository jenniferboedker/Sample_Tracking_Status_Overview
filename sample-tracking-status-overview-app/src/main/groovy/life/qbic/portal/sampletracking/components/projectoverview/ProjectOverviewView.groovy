package life.qbic.portal.sampletracking.components.projectoverview

import com.vaadin.data.provider.DataProvider
import com.vaadin.data.provider.ListDataProvider
import com.vaadin.event.selection.SingleSelectionEvent
import com.vaadin.icons.VaadinIcons
import com.vaadin.server.FileDownloader
import com.vaadin.server.StreamResource
import com.vaadin.shared.ui.ContentMode
import com.vaadin.shared.ui.grid.HeightMode
import com.vaadin.ui.*
import com.vaadin.ui.Grid.Column
import com.vaadin.ui.themes.ValoTheme
import groovy.util.logging.Log4j2
import life.qbic.portal.sampletracking.Constants
import life.qbic.portal.sampletracking.communication.notification.NotificationService
import life.qbic.portal.sampletracking.components.projectoverview.download.DownloadProjectController
import life.qbic.portal.sampletracking.components.projectoverview.samplelist.FailedQCSamplesView
import life.qbic.portal.sampletracking.components.projectoverview.samplelist.ProjectOverviewController
import life.qbic.portal.sampletracking.components.projectoverview.statusdisplay.SampleCount
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
@Log4j2
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
        connectFailedQcSamplesView()
        bindManifestToProjectSelection()
        this.addComponents(titleLabel,buttonBar, projectGrid, failedQCSamplesView)
    }

    private void connectFailedQcSamplesView() {
        FailedQCSamplesView samplesView = failedQCSamplesView
        showWhenFailingSamplesExist(samplesView)

        viewModel.addPropertyChangeListener("selectedProject", {
            Optional<ProjectSummary> selectedProject = Optional.ofNullable(viewModel.selectedProject)
            selectedProject.ifPresent({
                loadFailedQcSamples(it)
            })
            if (!selectedProject.isPresent()) {
                samplesView.reset()
            }
        })
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

        viewModel.addPropertyChangeListener("selectedProject", {
            if (failingSamplesExist()) {
                detailsButton.setEnabled(true)
            } else {
                detailsButton.setEnabled(false)
            }
        })

        detailsButton.addClickListener({
            loadFailedQcSamples(viewModel.selectedProject)
        })

        return detailsButton
    }

    private void loadFailedQcSamples(ProjectSummary projectSummary) {
        Optional<ProjectSummary> selectedProject = Optional.ofNullable(projectSummary)
        selectedProject
                .map({it.getCode()})
                .ifPresent(projectOverviewController::getFailedQcSamples)
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
        showWhenProjectIsSelected(subscriptionCheckBox)
        subscriptionCheckBox.setValue(false)
        viewModel.addPropertyChangeListener("selectedProject", {
            Optional<ProjectSummary> selectedProjectSummary = Optional.ofNullable(it.newValue as ProjectSummary)
            selectedProjectSummary.ifPresent({
                subscriptionCheckBox.value = it.hasSubscription
            })
            if (!selectedProjectSummary.isPresent()) {
                subscriptionCheckBox.value = false
            }

        })
        subscriptionCheckBox.addValueChangeListener(checkBoxValueChange -> {
            if (checkBoxValueChange.oldValue == checkBoxValueChange.value) {
                return // just to be sure a change is present
            }
            if (checkBoxValueChange.value) {
                subscribeIfNotSubscribed(viewModel.selectedProject)
            } else {
                unsubscribeIfSubscribed(viewModel.selectedProject)
            }
        })
        return subscriptionCheckBox
    }

    /**
     * Determines if a subscription is requested and triggers it
     * @param projectSummary the project summary to which a subscription might be requested
     */
    private void subscribeIfNotSubscribed(ProjectSummary projectSummary) {
        Optional<ProjectSummary> selectedProject = Optional.ofNullable(projectSummary)
        selectedProject
                .filter({ !it.hasSubscription })
                .ifPresent({ subscribeToProject(it.code) })
    }

    private void unsubscribeIfSubscribed(ProjectSummary projectSummary) {
        Optional<ProjectSummary> selectedProject = Optional.ofNullable(projectSummary)
        selectedProject
                .filter({ it.hasSubscription })
                .ifPresent({ unsubscribeFromProject(it.code) })
    }

    private void setupProjects() {
        projectGrid = new Grid<ProjectSummary>()
        fillProjectsGrid()
        projectGrid.setSelectionMode(Grid.SelectionMode.SINGLE)
        projectGrid.addSelectionListener({
            if (it instanceof SingleSelectionEvent<ProjectSummary>) {
                Optional<ProjectSummary> selectedItem = it.getSelectedItem()
                if (!selectedItem.isPresent()) {
                    clearProjectSelection()
                }
                selectedItem.ifPresent({
                    selectProject(it)
                })

            }
        })
        viewModel.updatedProjectsChannel.subscribe({updatedProjectCode ->
            refreshDataProvider()
        })
    }

    private void selectProject(ProjectSummary projectSummary) {
        if (!(projectSummary in viewModel.projectOverviews)) {
            selectProject(projectSummary.code)
        } else {
            viewModel.selectedProject = projectSummary
        }
    }
    private void selectProject(String projectCode) {
        Optional<ProjectSummary> projectSummary = Optional.ofNullable(viewModel.getProjectSummary(projectCode))
        projectSummary.ifPresent({
            viewModel.selectedProject = it
        })
        if (! projectSummary.isPresent()) {
            // we tried to select a project summary that is not in our list of project summaries
            // this should not happen
            throw new IllegalArgumentException("No project with code $projectCode could be selected." +
                    " The project was not found in our list of projects.")
        }
    }

    private void bindManifestToProjectSelection() {
        viewModel.addPropertyChangeListener("selectedProject", { tryToDownloadManifest() })
    }

    private void clearProjectSelection() {
        viewModel.selectedProject = null
    }

    private static String getStyleForColumn(SampleCount sampleStatusCount) {
        State state = determineCompleteness(sampleStatusCount)
        return state.getCssClass()
    }

    private void fillProjectsGrid() {
        projectGrid.addColumn({ it.code })
                .setCaption("Project Code").setId("ProjectCode").setMaximumWidth(
                MAX_CODE_COLUMN_WIDTH)
        projectGrid.addColumn({ it.title })
                .setCaption("Project Title").setId("ProjectTitle").setDescriptionGenerator({ProjectSummary project -> project.title})

        projectGrid.addColumn({it.samplesReceived}).setStyleGenerator({ProjectSummary project -> getStyleForColumn(project.samplesReceived)})
                .setCaption("Samples Received").setId("SamplesReceived")

        projectGrid.addColumn({it.samplesQc}).setStyleGenerator({ProjectSummary project -> getStyleForColumn(project.samplesQc)})
                .setCaption("Samples Passed QC").setId("SamplesPassedQc")

        projectGrid.addColumn({it.samplesLibraryPrepFinished}).setStyleGenerator({ProjectSummary project -> getStyleForColumn(project.samplesLibraryPrepFinished)})
                .setCaption("Library Prep Finished").setId("LibraryPrepFinished")

        projectGrid.addColumn({it.sampleDataAvailable}).setStyleGenerator({ProjectSummary project -> getStyleForColumn(project.sampleDataAvailable)})
                .setCaption("Data Available").setId("SampleDataAvailable")
        refreshDataProvider()
        //specify size of grid and layout
        projectGrid.setWidthFull()
        projectGrid.getColumn("ProjectTitle").setMaximumWidth(800)
        projectGrid.getColumn("SamplesReceived").setExpandRatio(1)
        projectGrid.getColumn("SamplesPassedQc").setExpandRatio(1)
        projectGrid.getColumn("LibraryPrepFinished").setExpandRatio(1)
        projectGrid.getColumn("SampleDataAvailable").setExpandRatio(1)

        projectGrid.setHeightMode(HeightMode.ROW)

        // remove manual sorting - any sorting in the code should probably done before disabling it
        for (Column col : projectGrid.getColumns()) {
          col.setSortable(false)
        }
    }

    private void refreshDataProvider() {
        DataProvider dataProvider = new ListDataProvider(viewModel.projectOverviews)
        projectGrid.setDataProvider(dataProvider)
        if( viewModel.selectedProject ) {
            projectGrid.select(viewModel.selectedProject)
        }
    }


    private void tryToDownloadManifest() {
        Optional<ProjectSummary> selectedSummary = Optional.empty()
        try {
            Optional<ProjectSummary> downloadableProject = Optional.ofNullable(viewModel.selectedProject).filter(
                    {
                        it.sampleDataAvailable.passingSamples > 0
                    })
            downloadableProject.ifPresent({
                String projectCode = it.getCode()
                downloadProjectController.downloadProject(projectCode)
            })
        } catch (IllegalArgumentException illegalArgument ) {
                String projectCode = selectedSummary.map(ProjectSummary::getCode).orElse(
                        "No project selected")
                notificationService.publishFailure("Manifest Download failed for project ${projectCode}. ${Constants.CONTACT_HELPDESK}")
                log.error "Manifest Download failed due to: ${illegalArgument.getMessage()}"
            } catch (Exception exception ) {
                notificationService.publishFailure("Manifest Download failed for unknown reasons. ${Constants.CONTACT_HELPDESK}")
                log.error "An error occured whily trying to download ${selectedSummary}"
                log.error "Manifest Download failed due to: ${exception.getMessage()}"
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

    private void showWhenProjectIsSelected(CheckBox checkBox) {
        viewModel.addPropertyChangeListener("selectedProject", {
            if(viewModel.selectedProject){
             checkBox.setVisible(true)
            }else{
              checkBox.setVisible(false)
            }
        })
    }

    private void showWhenFailingSamplesExist(Component component) {
        component.setVisible(failingSamplesExist())
        viewModel.addPropertyChangeListener("selectedProject", {
            component.setVisible(failingSamplesExist())
        })
    }

    private boolean failingSamplesExist() {
        Optional<ProjectSummary> selectedProject = Optional.ofNullable(viewModel.selectedProject)
        boolean hasFailingSamples = selectedProject
                .map({ it.samplesQc.failingSamples > 0 })
                .orElse(false)
        return hasFailingSamples
    }

    private void subscribeToProject(String projectCode) {
        if (viewModel.subscriber) {
            if (projectCode) {
                subscribeProjectController.subscribeProject(viewModel.subscriber, projectCode)
            }
        }
    }

    private void unsubscribeFromProject(String projectCode) {
        if (viewModel.subscriber) {
            if (projectCode) {
                subscribeProjectController.unsubscribeProject(viewModel.subscriber, projectCode)
            }
        }
    }

    /**
     * Determines the state of the current status. Is it in progress or did it complete already
     * @param sampleCount The total number of samples registered
     */
    private static State determineCompleteness(SampleCount sampleCount) {
        if (sampleCount.failingSamples > 0){
            return State.FAILED
        }
        else if (sampleCount.passingSamples == sampleCount.totalSampleCount) {
            return State.COMPLETED
        }
        else if (sampleCount.passingSamples < sampleCount.totalSampleCount) {
            return State.IN_PROGRESS
        }
        else {
            //unexpected!!
            throw new IllegalStateException("status count $sampleCount.passingSamples must not be greater total count $sampleCount.totalSampleCount")
        }
    }
}
