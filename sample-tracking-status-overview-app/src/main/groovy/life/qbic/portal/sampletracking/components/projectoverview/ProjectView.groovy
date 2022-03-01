package life.qbic.portal.sampletracking.components.projectoverview

import com.vaadin.data.provider.DataProvider
import com.vaadin.data.provider.ListDataProvider
import com.vaadin.event.selection.SingleSelectionEvent
import com.vaadin.icons.VaadinIcons
import com.vaadin.server.ClientConnector
import com.vaadin.server.FileDownloader
import com.vaadin.server.StreamResource
import com.vaadin.shared.data.sort.SortDirection
import com.vaadin.shared.ui.ContentMode
import com.vaadin.shared.ui.grid.HeightMode
import com.vaadin.ui.AbstractComponent
import com.vaadin.ui.Component
import com.vaadin.ui.Grid
import com.vaadin.ui.TextField
import com.vaadin.ui.components.grid.HeaderRow
import com.vaadin.ui.renderers.ComponentRenderer
import groovy.util.logging.Log4j2
import life.qbic.business.project.subscribe.Subscriber
import life.qbic.portal.sampletracking.Constants
import life.qbic.portal.sampletracking.communication.notification.NotificationService
import life.qbic.portal.sampletracking.components.GridUtils
import life.qbic.portal.sampletracking.components.GridUtilsImpl
import life.qbic.portal.sampletracking.components.ViewModel
import life.qbic.portal.sampletracking.components.projectoverview.download.DownloadProjectController
import life.qbic.portal.sampletracking.components.projectoverview.statusdisplay.SampleCount
import life.qbic.portal.sampletracking.components.projectoverview.statusdisplay.State
import life.qbic.portal.sampletracking.components.projectoverview.subscribe.SubscribeProjectController
import life.qbic.portal.sampletracking.components.projectoverview.subscribe.SubscriptionCheckboxFactory

import java.util.function.Consumer

@Log4j2
class ProjectView extends ProjectDesign {

    private final ViewModel viewModel
    private final DownloadProjectController downloadProjectController
    final static int MAX_CODE_COLUMN_WIDTH = 400
    private final SubscriptionCheckboxFactory subscriptionCheckboxFactory
    private final NotificationService notificationService
    private final ProjectFilter projectFilter = new ProjectFilterImpl().allowEmptyProjects(false)
    private final GridUtils gridUtils = new GridUtilsImpl()

    ProjectView(ViewModel viewModel, SubscribeProjectController subscribeProjectController, NotificationService notificationService, Subscriber subscriber, DownloadProjectController downloadProjectController) {
        super()
        this.viewModel = viewModel
        this.subscriptionCheckboxFactory = new SubscriptionCheckboxFactory(subscribeProjectController, subscriber, notificationService)
        this.downloadProjectController = downloadProjectController
        this.notificationService = notificationService
        bindData()
        addClickListener()
        setupDownloadButton()
        bindManifestToProjectSelection()
        setupLayoutResponsiveness(this)
        setDynamicResizing(true)
        setProjectGridStyles(projectGrid)
        addSorting()
        enableUserProjectFiltering()
    }

    private void bindData() {
        projectGrid.addColumn({ subscriptionCheckboxFactory.getSubscriptionCheckbox(it) }, new ComponentRenderer())
                .setCaption("Subscribe").setId("Subscription").setMaximumWidth(MAX_CODE_COLUMN_WIDTH)
                .setComparator((o1, o2) -> o1.hasSubscription <=> o2.hasSubscription)

        projectGrid.addColumn({ it.title })
                .setCaption("Project Title").setId("ProjectTitle").setDescriptionGenerator({ ProjectSummary project -> project.title })

        projectGrid.addColumn({ it.code })
                .setCaption("Project Code").setId("ProjectCode").setMaximumWidth(
                MAX_CODE_COLUMN_WIDTH)

        projectGrid.addColumn({ it.samplesReceived }).setStyleGenerator({ ProjectSummary project -> getStyleForColumn(project.samplesReceived) })
                .setCaption("Samples Received").setId("SamplesReceived")

        projectGrid.addColumn({ it.samplesQc }).setStyleGenerator({ ProjectSummary project -> getStyleForColumn(project.samplesQc) })
                .setCaption("Samples Passed QC").setId("SamplesPassedQc")

        projectGrid.addColumn({ it.samplesLibraryPrepFinished }).setStyleGenerator({ ProjectSummary project -> getStyleForColumn(project.samplesLibraryPrepFinished) })
                .setCaption("Library Prep Finished").setId("LibraryPrepFinished")

        projectGrid.addColumn({ it.sampleDataAvailable }).setStyleGenerator({ ProjectSummary project -> getStyleForColumn(project.sampleDataAvailable) })
                .setCaption("Data Available").setId("SampleDataAvailable")

        projectGrid.addColumn({ it.lastChanged }).setId("lastUpdated").setHidden(true)

        refreshDataProvider()
        //specify size of grid and layout
        projectGrid.setWidthFull()
        projectGrid.setHeightMode(HeightMode.ROW)

        // remove manual sorting - any sorting in the code should probably done before disabling it
        for (Grid.Column col : projectGrid.getColumns()) {
            col.setSortable(false)
        }
    }

    private static void setProjectGridStyles(Grid projectGrid) {
        setHeaderRowStyle(projectGrid.getDefaultHeaderRow())
        addTooltips(projectGrid.getDefaultHeaderRow())
        setColumnsStyle(projectGrid)
    }

    private static void addTooltips(HeaderRow headerRow) {
        headerRow.getCell("Subscription").setDescription("Select a project to get status updates per email.")
        headerRow.getCell("SamplesReceived").setDescription("Number of samples that arrived in the processing facility.")
        headerRow.getCell("SamplesPassedQc").setDescription("Number of samples that passed quality control.")
        headerRow.getCell("LibraryPrepFinished").setDescription("Number of samples where library prep has been finished.")
        headerRow.getCell("SampleDataAvailable").setDescription("Number of available raw datasets.")
    }

    private static void setHeaderRowStyle(HeaderRow headerRow) {
        headerRow.getCell("Subscription").setStyleName("cell-min-width header-with-tooltip")
        headerRow.getCell("SamplesReceived").setStyleName("cell-min-width header-with-tooltip")
        headerRow.getCell("SamplesPassedQc").setStyleName("cell-min-width header-with-tooltip")
        headerRow.getCell("LibraryPrepFinished").setStyleName("cell-min-width header-with-tooltip")
        headerRow.getCell("SampleDataAvailable").setStyleName("cell-min-width header-with-tooltip")
        headerRow.getCell("ProjectTitle").setStyleName("cell-min-width cell-max-width")
        headerRow.getCell("ProjectCode").setStyleName("cell-min-width")
        headerRow.getCell("Subscription").setStyleName("subscription-cell")
    }

    private static void setColumnsStyle(Grid projectGrid) {
        projectGrid.getColumn("ProjectTitle").setStyleGenerator(projectTitleColumn -> {
            return "cell-min-width cell-max-width"
        })
        projectGrid.getColumn("Subscription").setStyleGenerator(projectTitleColumn -> {
            return "subscription-cell"
        })
    }

    private void addSorting(){
        sort.setItems(["Newest Changes", "Oldest Changes", "Subscribed", "Not Subscribed"])

        sort.addValueChangeListener({
            if (it.value) {
                switch (it.value) {
                    case "Subscribed":
                        projectGrid.sort("Subscription", SortDirection.DESCENDING)
                        break
                    case "Not Subscribed":
                        projectGrid.sort("Subscription", SortDirection.ASCENDING)
                        break
                    case "Newest Changes":
                        projectGrid.sort("lastUpdated", SortDirection.DESCENDING)
                        break
                    case "Oldest Changes":
                        projectGrid.sort("lastUpdated", SortDirection.ASCENDING)
                        break
                    default:
                        projectGrid.clearSortOrder()
                }
            } else {
                projectGrid.clearSortOrder()
                //FYI because the dataprovider content is sorted by last updated this is how its sorted currently
            }
        })
    }

    private void bindManifestToProjectSelection() {
        viewModel.addPropertyChangeListener("selectedProject", { tryToDownloadManifest() })
    }

    private void refreshDataProvider() {
        ListDataProvider<ProjectSummary> dataProvider = new ListDataProvider(viewModel.projectSummaries)
        dataProvider.addFilter((ProjectSummary it) -> projectFilter.asPredicate().test(it))
        projectGrid.setDataProvider(dataProvider)
    }


    private static String getStyleForColumn(SampleCount sampleStatusCount) {
        State state = determineCompleteness(sampleStatusCount)
        return state.getCssClass()
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

    private void addClickListener() {
        projectGrid.addSelectionListener({
            if (it instanceof SingleSelectionEvent<ProjectSummary>) {
                Optional<ProjectSummary> selectedItem = it.getSelectedItem()
                if (!selectedItem.isPresent()) {
                    viewModel.selectedProject = null
                    samplesButton.setEnabled(false)
                }
                selectedItem.ifPresent({
                    viewModel.selectedProject = it
                    samplesButton.setEnabled(true)
                })
            }
        })
        projectGrid.setStyleGenerator(projectRow -> {
            return "clickable-row"
        })

        viewModel.updatedProjectsChannel.subscribe({ updatedProjectCode ->
            refreshDataProvider()
        })

        this.samplesButton.addClickListener({
            viewModel.projectViewEnabled = false
        })

    }

    private void setupDownloadButton() {
        downloadButton.setIcon(VaadinIcons.DOWNLOAD)
        downloadButton.setVisible(false)

        downloadButton.setDescription("A manifest is a text file with sample codes used by our client application to download the data attached to the defined samples. <br>" +
                "Use <a href=\"https://github.com/qbicsoftware/postman-cli\" target=\"_blank\">" + VaadinIcons.EXTERNAL_LINK.getHtml() + " qpostman</a> to download the sample data.", ContentMode.HTML)

        viewModel.addPropertyChangeListener("generatedManifest", {
            if (it.getOldValue() != it.getNewValue()) {
                removeFileDownloaders(downloadButton)
                if (it.newValue) {
                    FileDownloader fileDownloader = new FileDownloader(new StreamResource({ viewModel.getManifestInputStream() }, "manifest.txt"))
                    fileDownloader.extend(downloadButton)
                }
            }
        })
        enableWhenDownloadIsAvailable(downloadButton)
    }

    private static void removeFileDownloaders(ClientConnector clientConnector) {
        def fileDownloaders = clientConnector.extensions.stream()
                .filter((extension) -> extension instanceof FileDownloader)
                .collect()
        fileDownloaders.forEach(clientConnector::removeExtension)
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
            downloadButton.setVisible(downloadableProject.isPresent())
        } catch (IllegalArgumentException illegalArgument) {
            String projectCode = selectedSummary.map(ProjectSummary::getCode).orElse(
                    "No project selected")
            notificationService.publishFailure("Manifest Download failed for project ${projectCode}. ${Constants.CONTACT_HELPDESK}")
            log.error "Manifest Download failed due to: ${illegalArgument.getMessage()}"
        } catch (Exception exception) {
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

    /**
     * With change of the selectedProject property in the viewmodel this method calls the consumer and provides him
     * with the selected project summary
     * @param projectConsumer The consumer that will accept the selected project summary
     */
    void onSelectedProjectChange(Consumer<ProjectSummary> projectConsumer) {
        viewModel.addPropertyChangeListener("selectedProject", {
            projectConsumer.accept(viewModel.selectedProject)
        })
    }

    void enableUserProjectFiltering() {
        TextField searchField = this.searchField
        searchField.addValueChangeListener({
            if (it.getValue()) {
                projectFilter.containingText(it.getValue())
            } else {
                projectFilter.containingText("")
            }
            currentDataProvider().refreshAll()
        })
    }

    private DataProvider<ProjectSummary, ?> currentDataProvider() {
        DataProvider<ProjectSummary, ?> dataProvider = this.projectGrid.getDataProvider()
        return dataProvider
    }

    /**
     * Adds responsiveness to an abstractComponent
     *
     * <p>This applies the css class style .responsive-grid-layout to the provided abstractComponent allowing it to display it's content in a responsive manner</p>
     *
     * @param AbstractComponent the {@link com.vaadin.ui.AbstractComponent}, where the css style and responsiveness should be added
     * @since 1.0.2
     */
    static void setupLayoutResponsiveness(AbstractComponent abstractComponent) {
        abstractComponent.addStyleName("responsive-grid-layout")
        abstractComponent.setWidthFull()
    }

    /**
     * Disables manual resizing of individual grid columns and calculates column width dependent on screen size
     *
     *
     * @param isDynamicResizing boolean value determining if a grid should be manually or automatically resizable
     * @since 1.0.2
     */
    private void setDynamicResizing(boolean isDynamicResizing) {
        if (isDynamicResizing) {
            gridUtils.disableResizableColumns(projectGrid)
            gridUtils.enableDynamicResizing(projectGrid)
        }
        else {
            gridUtils.enableResizableColumns(projectGrid)
            gridUtils.disableDynamicResizing(projectGrid)
        }
    }
}
