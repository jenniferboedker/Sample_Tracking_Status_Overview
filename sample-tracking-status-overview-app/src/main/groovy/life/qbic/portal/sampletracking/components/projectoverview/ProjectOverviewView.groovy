package life.qbic.portal.sampletracking.components.projectoverview

import com.vaadin.data.provider.DataProvider
import com.vaadin.data.provider.ListDataProvider
import com.vaadin.event.selection.SingleSelectionEvent
import com.vaadin.icons.VaadinIcons
import com.vaadin.server.ClientConnector
import com.vaadin.server.FileDownloader
import com.vaadin.server.StreamResource
import com.vaadin.shared.ui.ContentMode
import com.vaadin.shared.ui.MarginInfo
import com.vaadin.shared.ui.grid.HeightMode
import com.vaadin.ui.*
import com.vaadin.ui.Grid.Column
import com.vaadin.ui.renderers.ComponentRenderer
import groovy.util.logging.Log4j2
import life.qbic.portal.sampletracking.Constants
import life.qbic.portal.sampletracking.communication.notification.NotificationService
import life.qbic.portal.sampletracking.components.GridUtils
import life.qbic.portal.sampletracking.components.HasHotbar
import life.qbic.portal.sampletracking.components.HasTitle
import life.qbic.portal.sampletracking.components.projectoverview.download.DownloadProjectController
import life.qbic.portal.sampletracking.components.projectoverview.samplelist.FailedQCSamplesController
import life.qbic.portal.sampletracking.components.projectoverview.samplelist.FailedQCSamplesView
import life.qbic.portal.sampletracking.components.projectoverview.statusdisplay.SampleCount
import life.qbic.portal.sampletracking.components.projectoverview.statusdisplay.State
import life.qbic.portal.sampletracking.components.projectoverview.subscribe.SubscribeProjectController
import life.qbic.portal.sampletracking.components.projectoverview.subscribe.SubscriptionCheckboxFactory

import java.util.function.Consumer

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
class ProjectOverviewView extends VerticalLayout implements HasHotbar, HasTitle {

    private static final String TITLE = "Project Overview"

    private final ProjectOverviewViewModel viewModel
    private final DownloadProjectController downloadProjectController
    private final SubscribeProjectController subscribeProjectController
    private final NotificationService notificationService
    private final FailedQCSamplesView failedQCSamplesView
    private final FailedQCSamplesController failedQCSamplesController

    private final SubscriptionCheckboxFactory subscriptionCheckboxFactory

    private Grid<ProjectSummary> projectGrid
    private HorizontalSplitPanel splitPanel
    private static final Collection<String> columnIdsWithFilters = ["ProjectCode", "ProjectTitle"]

    final static int MAX_CODE_COLUMN_WIDTH = 400
    private HorizontalLayout projectButtonBar = new HorizontalLayout()

    ProjectOverviewView(NotificationService notificationService, ProjectOverviewViewModel viewModel, DownloadProjectController downloadProjectController
                        , FailedQCSamplesView failedQCSamplesView, FailedQCSamplesController failedQCSamplesController, SubscribeProjectController subscribeProjectController){
        this.notificationService = notificationService
        this.viewModel = viewModel
        this.downloadProjectController = downloadProjectController
        this.subscribeProjectController = subscribeProjectController
        this.failedQCSamplesView = failedQCSamplesView
        this.failedQCSamplesController = failedQCSamplesController

        this.subscriptionCheckboxFactory = new SubscriptionCheckboxFactory(subscribeProjectController, viewModel.subscriber,notificationService)

        initLayout()
    }

    /**
     * With change of the selectedProject property in the viewmodel this method calls the consumer and provides him
     * with the selected project summary
     * @param projectConsumer The consumer that will accept the selected project summary
     */
    void onSelectedProjectChange(Consumer<ProjectSummary> projectConsumer){
        viewModel.addPropertyChangeListener("selectedProject", {
            projectConsumer.accept(viewModel.selectedProject)
        })
    }

    private void initLayout(){
        setupProjects()

        setupButtonLayout(projectButtonBar)
        VerticalLayout projectLayout = new VerticalLayout(projectGrid)
        projectLayout.setMargin(false)

        splitPanel = createSplitLayout(projectLayout,failedQCSamplesView)
        failedQCSamplesView.addVisibilityChangeListener({ splitPanel.splitPosition = it.newValue ? 65 : 100 })

        connectFailedQcSamplesView()
        bindManifestToProjectSelection()
        this.addComponents(splitPanel)
        this.setMargin(false)
    }

    /**
     * Should be called after double-clicking a project. The provided consumer will be called and the performs the action
     * on the double clicked project
     * @param consumer The consumer accepts the double-clicked project and performs action
     */
    void onProjectDoubleClick(Consumer<ProjectSummary> consumer){
         projectGrid.addItemClickListener({
             //if grid.getEditor().setEnabled(true) is enabled this will not work anymore!
             if(it.mouseEventDetails.isDoubleClick()){
                 if(viewModel.selectedProject) consumer.accept(viewModel.selectedProject)
             }
         })
    }

    private void connectFailedQcSamplesView() {
        FailedQCSamplesView samplesView = failedQCSamplesView
        showWhenFailingSamplesExist(samplesView)

        viewModel.addPropertyChangeListener("selectedProject", {
            Optional<ProjectSummary> selectedProject = Optional.ofNullable(viewModel.selectedProject)
            selectedProject.ifPresent({
                if(failingSamplesExist()){
                    loadFailedQcSamples(it)
                }
            })
            if (!selectedProject.isPresent()) {
                samplesView.reset()
            }
        })
    }

    private HorizontalLayout setupButtonLayout(HorizontalLayout buttonBar) {
        buttonBar.setMargin(false)

        Button postmanLink = setUpLinkButton()
        Button downloadManifestAction = setupDownloadButton()

        HorizontalLayout downloadLayout = new HorizontalLayout(downloadManifestAction,postmanLink)
        downloadLayout.setComponentAlignment(downloadManifestAction, Alignment.MIDDLE_CENTER)
        downloadLayout.setComponentAlignment(postmanLink, Alignment.MIDDLE_CENTER)
        downloadLayout.setSpacing(false)

        buttonBar.addComponents(downloadLayout)
        buttonBar.setComponentAlignment(downloadLayout, Alignment.MIDDLE_CENTER)
        return buttonBar
    }

    private void loadFailedQcSamples(ProjectSummary projectSummary) {
        String code = projectSummary.getCode()
        failedQCSamplesController.getFailedQcSamples(code)
    }

    private Button setUpLinkButton(){
        Button button = new Button()
        button.setIcon(VaadinIcons.QUESTION_CIRCLE)
        button.setStyleName("round-button")

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
            if (it.getOldValue() != it.getNewValue()) {
                removeFileDownloaders(downloadManifestAction)
                if (it.newValue) {
                    FileDownloader fileDownloader = new FileDownloader(new StreamResource({viewModel.getManifestInputStream()}, "manifest.txt"))
                    fileDownloader.extend(downloadManifestAction)
                }
            }
        })
        enableWhenDownloadIsAvailable(downloadManifestAction)
        return downloadManifestAction
    }

    private static void removeFileDownloaders(ClientConnector clientConnector) {
        clientConnector.extensions.stream()
                .filter((extension) -> extension instanceof FileDownloader)
                .forEach(clientConnector::removeExtension)
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
        projectGrid.setStyleGenerator(projectRow -> {
            return "clickable-row"
        })
        viewModel.updatedProjectsChannel.subscribe({updatedProjectCode ->
            refreshDataProvider()
        })
    }

    private void filterEmptyProjects(){
        ListDataProvider<ProjectSummary> dataProvider = (ListDataProvider<ProjectSummary>) projectGrid.getDataProvider()
        dataProvider.setFilter(ProjectSummary::getTotalSampleCount, totalNumber -> totalNumber > 0)
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

    private static HorizontalSplitPanel createSplitLayout(Layout leftComponent, VerticalLayout rightComponent){
        HorizontalSplitPanel splitPanel = new HorizontalSplitPanel(leftComponent,rightComponent)
        splitPanel.setSplitPosition(100)
        rightComponent.setMargin(new MarginInfo(false,false,false,true))

        return splitPanel
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
        projectGrid.addColumn({ subscriptionCheckboxFactory.getSubscriptionCheckbox(it)}, new ComponentRenderer())
                .setCaption("Subscription Status").setId("Subscription").setMaximumWidth(MAX_CODE_COLUMN_WIDTH).setStyleGenerator({"subscription-checkbox"})
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
        GridUtils.setupFilters(projectGrid, columnIdsWithFilters)

        filterEmptyProjects()
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


    /**
     * Determines the state of the current status. Is it in progress or did it complete already
     * @param sampleCount The total number of samples registered
     */
    private static State determineCompleteness(SampleCount sampleCount) {
        if (sampleCount.failingSamples > 0) {
            return State.FAILED
        } else if (sampleCount.totalSampleCount == 0) {
            return State.IN_PROGRESS
        } else if (sampleCount.passingSamples == sampleCount.totalSampleCount) {
            return State.COMPLETED
        } else if (sampleCount.passingSamples < sampleCount.totalSampleCount) {
            return State.IN_PROGRESS
        } else {
            //unexpected!!
            throw new IllegalStateException("status count $sampleCount.passingSamples must not be greater total count $sampleCount.totalSampleCount")
        }
    }

    @Override
    void setVisible(boolean visible) {
        super.setVisible(visible)
        getHotbar().setVisible(visible)
    }

    HorizontalLayout getHotbar() {
        return projectButtonBar
    }

    @Override
    String getTitle() {
        return TITLE
    }
}
