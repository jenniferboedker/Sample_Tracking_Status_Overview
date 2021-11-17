package life.qbic.portal.sampletracking.components.projectoverview.samplelist


import com.vaadin.data.provider.DataProvider
import com.vaadin.icons.VaadinIcons
import com.vaadin.shared.ui.grid.HeightMode
import com.vaadin.ui.*
import life.qbic.business.samples.Sample
import life.qbic.business.samples.info.GetSamplesInfoOutput
import life.qbic.datamodel.samples.Status
import life.qbic.portal.sampletracking.communication.notification.NotificationService
import life.qbic.portal.sampletracking.components.visibility.VisibilityChangeEvent
import life.qbic.portal.sampletracking.components.visibility.VisibilityChangeListener

/**
 * <b>Shows the failed QC samples </b>
 *
 * @since 1.0.0
 */
class FailedQCSamplesView extends VerticalLayout {
    private final ViewModel viewModel
    private final Presenter presenter

    private Grid<Sample> samplesGrid
    private final List<VisibilityChangeListener> visibilityChangeListeners = []

    FailedQCSamplesView(NotificationService notificationService) {
        this.viewModel = new ViewModel()
        this.presenter = new Presenter(viewModel, notificationService)
        initLayout()
    }

    private void initLayout() {
        this.setMargin(false)
        createSamplesGrid()
        HorizontalLayout buttonLayout = setupCloseButtonLayout()

        this.addComponents(buttonLayout,samplesGrid)
    }

    void addVisibilityChangeListener(VisibilityChangeListener listener){
        visibilityChangeListeners.add(listener)
    }

    private HorizontalLayout setupCloseButtonLayout() {
        Button closeButton = new Button("Hide", VaadinIcons.CLOSE_CIRCLE)
        closeButton.addClickListener({
            this.setVisible(false)
        })

        HorizontalLayout buttonLayout = new HorizontalLayout()
        buttonLayout.addComponent(closeButton)
        buttonLayout.setComponentAlignment(closeButton, Alignment.TOP_LEFT)

        return buttonLayout
    }

    @Override
    void setVisible(boolean visible) {
        boolean currentlyVisible = isVisible()
        if (currentlyVisible != visible) {
            VisibilityChangeEvent changeEvent = new VisibilityChangeEvent(this, this.isVisible(), visible)
            fireVisibilityChangeEvent(changeEvent)
        }
        super.setVisible(visible)
    }

    private void fireVisibilityChangeEvent(VisibilityChangeEvent changeEvent) {
        visibilityChangeListeners.forEach({ it.visibilityChanged(changeEvent) })
    }

    /**
     * Resets the view to its initial state.
     * @since 1.0.0
     */
    void reset() {
        if (viewModel.samples.size() > 0) {
            viewModel.samples.clear()
        }
    }

    private void createSamplesGrid() {

        this.samplesGrid = new Grid<>()
        samplesGrid.addColumn(Sample::getCode).setCaption("Failed QC Sample Code").setId("SampleCode")
        samplesGrid.addColumn(Sample::getName).setCaption("Sample Name").setId("SampleName")
        samplesGrid.setSelectionMode(Grid.SelectionMode.NONE)
        samplesGrid.setDataProvider(DataProvider.ofCollection(viewModel.getSamples()))
        samplesGrid.setHeightMode(HeightMode.ROW)
    }

    GetSamplesInfoOutput getPresenter() {
        return this.presenter
    }

    private static class ViewModel {
        List<Sample> samples = new ArrayList<>()
    }

    private static class Presenter implements GetSamplesInfoOutput {

        private final ViewModel viewModel
        private final NotificationService notificationService


        Presenter(ViewModel viewModel, NotificationService notificationService) {
            this.viewModel = viewModel
            this.notificationService = notificationService
        }

        @Override
        void failedExecution(String reason) {
            notificationService.publishFailure(reason)
        }

        /**
         * To be called after successfully fetching sample codes with respective sample names for the provided project and status.
         * @param samples a collection of samples with names
         * @since 1.0.0
         */
        @Override
        void samplesWithNames(Collection<Sample> samples) {
            List<Sample> failedSamples = samples.stream().filter({it.status == Status.SAMPLE_QC_FAIL}).collect()
            viewModel.samples.clear()
            viewModel.samples.addAll(failedSamples)
        }
    }
}
