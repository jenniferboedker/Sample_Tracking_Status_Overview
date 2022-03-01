package life.qbic.portal.sampletracking.components.sampleoverview

import com.vaadin.data.provider.DataProvider
import com.vaadin.data.provider.ListDataProvider
import com.vaadin.ui.AbstractComponent
import com.vaadin.ui.ComboBox
import com.vaadin.ui.Grid
import com.vaadin.ui.TextField
import com.vaadin.ui.components.grid.HeaderRow
import life.qbic.business.samples.Sample
import life.qbic.business.samples.info.GetSamplesInfoOutput
import life.qbic.datamodel.samples.Status
import life.qbic.portal.sampletracking.communication.notification.NotificationService
import life.qbic.portal.sampletracking.components.GridUtils
import life.qbic.portal.sampletracking.components.GridUtilsImpl
import life.qbic.portal.sampletracking.components.Responsive
import life.qbic.portal.sampletracking.components.ViewModel
import life.qbic.portal.sampletracking.components.projectoverview.statusdisplay.State

class SampleView extends SampleDesign implements Responsive {

    private final ViewModel viewModel
    private final Presenter presenter
    private final SampleFilter sampleFilter = new SampleFilterImpl()
    private final GridUtils gridUtils = new GridUtilsImpl()

    SampleView(ViewModel viewModel, NotificationService notificationService) {
        super()
        this.viewModel = viewModel
        this.presenter = new Presenter(notificationService, viewModel)
        init()
    }

    private void init() {
        activateViewToggle()
        createSamplesGrid()
        enableUserSampleFiltering()
        addColumnColoring()
        setDynamicResizing(true)
        setupLayoutResponsiveness(this)
        setSampleGridStyle(sampleGrid)
    }

    private void activateViewToggle() {
        this.projectsButton.addClickListener({
            viewModel.projectViewEnabled = true
        })

        viewModel.addPropertyChangeListener("projectViewEnabled", {
            if (viewModel.projectViewEnabled) {
                this.projectsButton.setEnabled(false)
            } else {
                this.projectsButton.setEnabled(true)
            }
        })
    }


    private void createSamplesGrid() {
        sampleGrid.setWidthFull()
        ListDataProvider<Sample> dataProvider = ListDataProvider.ofCollection(viewModel.samples)
        dataProvider.addFilter((Sample it) -> sampleFilter.asPredicate().test(it))
        sampleGrid.setDataProvider(dataProvider)
        sampleGrid.getColumn("name").setExpandRatio(3)
        sampleGrid.getColumn("code").setExpandRatio(1)
        sampleGrid.getColumn("status").setExpandRatio(2)
    }

    void reset() {
        viewModel.samples.clear()
    }

    Presenter getPresenter() {
        return presenter
    }

    private static String determineColor(Status status) {
        switch (status) {
            case Status.DATA_AVAILABLE:
                return State.COMPLETED.getCssClass()
            case Status.SAMPLE_QC_FAIL:
                return State.FAILED.getCssClass()
            default:
                return State.IN_PROGRESS.getCssClass()
        }
    }

    private void addColumnColoring() {
        sampleGrid.getColumn("status").setStyleGenerator({ Sample sample -> determineColor(sample.status) })
    }

    void enableUserSampleFiltering() {
        enableUserFilterByStatus()
        enableUserFilterBySearchbar()
    }

    private void enableUserFilterByStatus() {
        ComboBox<Status> statusComboBox = this.statusComboBox

        statusComboBox.setItems(Status.METADATA_REGISTERED,
                Status.SAMPLE_RECEIVED,
                Status.SAMPLE_QC_FAIL,
                Status.SAMPLE_QC_PASS,
                Status.LIBRARY_PREP_FINISHED,
                Status.DATA_AVAILABLE)
        statusComboBox.setItemCaptionGenerator({ it.getDisplayName() })

        DataProvider<Sample, ?> dataProvider = this.sampleGrid.getDataProvider()
        statusComboBox.addValueChangeListener({
            if (it.getValue()) {
                sampleFilter.withStatus(it.getValue().toString())
            } else {
                sampleFilter.clearStatus()
            }
            dataProvider.refreshAll()
        })
    }

    void enableUserFilterBySearchbar() {
        TextField searchField = this.searchField
        DataProvider<Sample, ?> dataProvider = this.sampleGrid.getDataProvider()
        searchField.addValueChangeListener({
            if (it.getValue()) {
                sampleFilter.containingText(it.getValue())
            } else {
                sampleFilter.containingText("")
            }
            dataProvider.refreshAll()
        })
    }

    private static void setSampleGridStyle(Grid sampleGrid) {
        setHeaderRowStyle(sampleGrid.getDefaultHeaderRow())
    }

    private static void setHeaderRowStyle(HeaderRow headerRow) {
        headerRow.getCell("name").setStyleName("cell-min-width")
        headerRow.getCell("code").setStyleName("cell-min-width")
        headerRow.getCell("status").setStyleName("cell-min-width")
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
     * @param isDynamicResizing boolean value determining if a grid should be manually or automatically resizable
     * @since 1.0.2
     */
    private void setDynamicResizing(boolean isDynamicResizing) {
        if (isDynamicResizing) {
            gridUtils.disableResizableColumns(sampleGrid)
            gridUtils.enableDynamicResizing(sampleGrid)
        }
        else {
            gridUtils.enableResizableColumns(sampleGrid)
            gridUtils.disableDynamicResizing(sampleGrid)
        }
    }

    @Override
    void enableResizableColumns() {

    }

    @Override
    void disableResizableColumns() {

    }

    @Override
    void enableDynamicResizing() {

    }

    @Override
    void disableDynamicResizing() {

    }

    /**
     * Presenter filling the grid model with information*/
    private static class Presenter implements GetSamplesInfoOutput {
        private final NotificationService notificationService
        private final ViewModel viewModel

        Presenter(NotificationService notificationService, ViewModel viewModel) {
            this.notificationService = notificationService
            this.viewModel = viewModel
        }

        @Override
        void failedExecution(String reason) {
            notificationService.publishFailure("Could not load samples: $reason")
        }

        @Override
        void samplesWithNames(Collection<Sample> samples) {
            viewModel.samples.clear()
            viewModel.samples.addAll(samples)
        }
    }
}
