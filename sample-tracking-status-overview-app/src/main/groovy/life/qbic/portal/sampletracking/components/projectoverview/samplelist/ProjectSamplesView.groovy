package life.qbic.portal.sampletracking.components.projectoverview.samplelist


import com.vaadin.data.provider.ListDataProvider
import com.vaadin.shared.ui.grid.HeightMode
import com.vaadin.ui.Grid
import com.vaadin.ui.VerticalLayout
import life.qbic.portal.sampletracking.communication.notification.NotificationService

/**
 * <b>A simple view containing a list of samples</b>
 *
 * @since 1.0.0
 */
class ProjectSamplesView extends VerticalLayout{
    private final ViewModel viewModel
    private final Presenter presenter
    private Grid<Sample> samplesGrid

    ProjectSamplesView(NotificationService notificationService) {
        this.viewModel = new ViewModel()
        this.presenter = new Presenter(notificationService, viewModel)
        initLayout()
    }

    private void initLayout() {
        this.setMargin(false)
        this.samplesGrid = createSamplesGrid(viewModel.samples)
        this.addComponents(samplesGrid)
    }

    Presenter getPresenter() {
        return presenter
    }

    private static Grid<Sample> createSamplesGrid(Collection<Sample> samples) {
        Grid<Sample> samplesGrid = new Grid<>()
        ListDataProvider<Sample> dataProvider = ListDataProvider.ofCollection(samples)
        samplesGrid.addColumn(Sample::getCode).setCaption("Sample Code").setId("SampleCode")
        samplesGrid.addColumn(Sample::getTitle).setCaption("Sample Title").setId("SampleTitle")
        samplesGrid.setSelectionMode(Grid.SelectionMode.NONE)
        samplesGrid.setDataProvider(dataProvider)
        samplesGrid.setHeightMode(HeightMode.ROW)
        samplesGrid.setSizeFull()
        return samplesGrid
    }

    /**
     * Bean for the Grid to be displayed
     */
    public static class Sample {
        String code
        String title

        Sample(String code, String title) {
            this.code = code
            this.title = title
        }
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
    private static class Presenter implements DummyOutput/*implements MyOutputInterface */{
        private final NotificationService notificationService
        private final ViewModel viewModel

        Presenter(NotificationService notificationService, ViewModel viewModel) {
            this.notificationService = notificationService
            this.viewModel = viewModel
        }

        //TODO remove
        @Override
        void consumeSamples(List<Sample> samples) {
            viewModel.samples.clear()
            viewModel.samples.addAll(samples)
        }
    }


    //TODO remove
    public static interface DummyOutput {
        void consumeSamples(List<Sample> samples)
    }
}


