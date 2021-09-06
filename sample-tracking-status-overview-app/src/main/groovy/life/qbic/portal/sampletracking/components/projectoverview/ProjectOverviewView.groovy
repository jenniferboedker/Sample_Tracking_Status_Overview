package life.qbic.portal.sampletracking.components.projectoverview

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
        Component component = setupProjectSpecificButtons()
        this.addComponents(titleLabel, component, projectGrid)
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

        CheckBox subscriptionCheckBox = new CheckBox("Subscribed")
        subscriptionCheckBox.setVisible(false)
        enableWhenProjectIsSelected(subscriptionCheckBox)
        subscriptionCheckBox.addValueChangeListener(event -> {
            changeSubscriberStatus(viewModel.projectIsSubscribed)
        })
        return subscriptionCheckBox
    }


    private void setupProjects() {
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
        HorizontalLayout buttonBar = new HorizontalLayout()
        buttonBar.setMargin(false)
        Button postmanLink = setUpLinkButton()
        Button downloadManifestAction = setupDownloadButton()
        CheckBox subscriptionCheckBox = setupSubscriptionCheckBox()
        buttonBar.addComponents(downloadManifestAction, postmanLink, subscriptionCheckBox)
        buttonBar.setComponentAlignment(postmanLink, Alignment.MIDDLE_CENTER)
        buttonBar.setComponentAlignment(subscriptionCheckBox, Alignment.MIDDLE_CENTER)
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

    private void enableWhenProjectIsSelected(Component component) {
        viewModel.addPropertyChangeListener("selectedProject") {
            Optional<ProjectSummary> projectSelection = Optional.ofNullable(viewModel.selectedProject)
            component.enabled = projectSelection.present
            component.visible = projectSelection.present
        }
    }

    private void changeSubscriberStatus(Boolean isSubscribed) {
        try {
            //ToDo This will be removed once the controller/UseCase/Presenter is in place
            viewModel.projectIsSubscribed = !isSubscribed
            if (isSubscribed) {
                println("Insert SubscriptionController.unsubscribe call here")
            } else {
                println("Insert SubscriptionController.subscribe call here")
            }
        }
        catch (IllegalArgumentException illegalArgument) {
            notificationService.publishFailure("Change in subscription status failed due to: ${illegalArgument.getMessage()}")
        } catch (Exception ignored) {
            notificationService.publishFailure("Subscription status change failed for unknown reasons. ${Constants.CONTACT_HELPDESK}")
        }

    }
}
