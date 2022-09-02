package life.qbic.portal.sampletracking.old.components.sampleoverview

import com.vaadin.data.provider.DataProvider
import com.vaadin.data.provider.ListDataProvider
import com.vaadin.ui.ComboBox
import com.vaadin.ui.TextField
import com.vaadin.ui.components.grid.HeaderRow
import life.qbic.business.samples.Sample
import life.qbic.business.samples.info.GetSamplesInfoOutput
import life.qbic.datamodel.samples.Status
import life.qbic.portal.sampletracking.old.communication.notification.NotificationService
import life.qbic.portal.sampletracking.old.components.Responsive
import life.qbic.portal.sampletracking.old.components.ViewModel
import life.qbic.portal.sampletracking.view.projects.State
import life.qbic.portal.sampletracking.view.samples.SampleDesign

class SampleView extends SampleDesign implements Responsive {

    private final ViewModel viewModel
    private final Presenter presenter
    private final SampleFilter sampleFilter = new SampleFilterImpl()

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
        setupLayoutResponsiveness()
        setSampleGridStyle()
        disableResizableColumns()
        enableDynamicResizing()
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

    private void enableUserSampleFiltering() {
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

    private void enableUserFilterBySearchbar() {
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

    private void setSampleGridStyle() {
        setHeaderRowStyle()
    }

    private void setHeaderRowStyle() {
        HeaderRow headerRow = sampleGrid.getDefaultHeaderRow()
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
    private void setupLayoutResponsiveness() {
            this.addStyleName("responsive-grid-layout")
            this.setWidthFull()
    }

    @Override
    void enableResizableColumns() {
        sampleGrid.getColumns().each { it ->
            {
                it.setResizable(true)
            }
        }
    }

    @Override
    void disableResizableColumns() {
        sampleGrid.getColumns().each { it ->
            {
                it.setResizable(false)
            }
        }
    }

    @Override
    void enableDynamicResizing() {
        sampleGrid.addAttachListener(attachEvent -> {
            sampleGrid.getUI().getCurrent().getPage().addBrowserWindowResizeListener(resizeEvent -> {
                sampleGrid.recalculateColumnWidths()
            })
        })
    }

    @Override
    void disableDynamicResizing() {
        Collection<AttachListener> attachListeners = sampleGrid.getListeners(AttachListener) as Collection<AttachListener>
        attachListeners.each {attachListener -> {
            sampleGrid.removeAttachListener(attachListener)
        }}
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