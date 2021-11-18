package life.qbic.portal.sampletracking.components.sampleoverview

import com.vaadin.data.provider.ListDataProvider
import com.vaadin.shared.ui.grid.HeightMode
import com.vaadin.ui.Grid
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.VerticalLayout
import life.qbic.business.samples.Sample
import life.qbic.business.samples.info.GetSamplesInfoOutput
import life.qbic.portal.sampletracking.communication.notification.NotificationService
import life.qbic.portal.sampletracking.components.HasHotbar
import life.qbic.portal.sampletracking.components.HasTitle
import life.qbic.portal.sampletracking.components.Resettable

/**
 * <b>A view component showing a list of samples</b>
 *
 * @since 1.0.0
 */
class SampleOverviewView extends VerticalLayout implements HasHotbar, HasTitle, Resettable {

    private static final String TITLE = "Sample Overview"

    private final ViewModel viewModel
    private final Presenter presenter
    private Grid<Sample> samplesGrid
    private final HorizontalLayout hotbar = new HorizontalLayout()

    SampleOverviewView(NotificationService notificationService) {
        this.viewModel = new ViewModel()
        this.presenter = new Presenter(notificationService, viewModel)

        initLayout()
    }

    private void initLayout() {
        this.setMargin(false)
        this.setSizeFull()

        this.samplesGrid = createSamplesGrid(viewModel.samples)
        samplesGrid.setSizeFull()
        this.addComponents(samplesGrid)
    }

    Presenter getPresenter() {
        return presenter
    }

    private static Grid<Sample> createSamplesGrid(Collection<Sample> samples) {
        Grid<Sample> samplesGrid = new Grid<>()
        ListDataProvider<Sample> dataProvider = ListDataProvider.ofCollection(samples)
        samplesGrid.addColumn(Sample::getCode).setCaption("Sample Code").setId("SampleCode")
        samplesGrid.addColumn(Sample::getName).setCaption("Sample Name").setId("SampleName")
        samplesGrid.addColumn(Sample::getStatus).setCaption("Sample Status").setId("SampleStatus")
        samplesGrid.setSelectionMode(Grid.SelectionMode.NONE)
        samplesGrid.setDataProvider(dataProvider)
        samplesGrid.setHeightMode(HeightMode.ROW)
        return samplesGrid
    }

    /**
     * Model for this view component
     */
    private static class ViewModel {
        List<Sample> samples = []
    }

    /**
     * Presenter filling the grid model with information
     */
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

    @Override
    HorizontalLayout getHotbar() {
        return hotbar
    }

    @Override
    String getTitle() {
        return TITLE
    }

    @Override
    void reset() {
        viewModel.samples.clear()
    }

}
