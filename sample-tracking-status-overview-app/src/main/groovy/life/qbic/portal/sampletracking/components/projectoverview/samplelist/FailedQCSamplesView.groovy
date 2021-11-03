package life.qbic.portal.sampletracking.components.projectoverview.samplelist

import com.vaadin.data.provider.DataProvider
import com.vaadin.icons.VaadinIcons
import com.vaadin.shared.ui.grid.HeightMode
import com.vaadin.ui.*
import life.qbic.business.samples.info.GetSamplesInfoOutput
import life.qbic.datamodel.samples.Status
import life.qbic.portal.sampletracking.communication.notification.NotificationService
import life.qbic.portal.sampletracking.components.projectoverview.visibility.VisibilityChangeListener

/**
 * <b>Shows the failed QC samples </b>
 *
 * @since 1.0.0
 */
class FailedQCSamplesView extends VerticalLayout {
    private final ViewModel viewModel
    private final Presenter presenter

    private Grid<Sample> samplesGrid
    final List<VisibilityChangeListener> visibilityChangeListener = []

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
        visibilityChangeListener.each {it.visibilityChangeEvent(visible,this.visible)}
        super.setVisible(visible)
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
        samplesGrid.addColumn(Sample::getTitle).setCaption("Sample Title").setId("SampleTitle")
        samplesGrid.setSelectionMode(Grid.SelectionMode.NONE)
        samplesGrid.setDataProvider(DataProvider.ofCollection(viewModel.getSamples()))
        samplesGrid.setHeightMode(HeightMode.ROW)
    }

    GetSamplesInfoOutput getPresenter() {
        return this.presenter
    }

    private static class Sample {
        final String code
        final String title

        Sample(String code, String title) {
            this.code = code
            this.title = title
        }
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
         * @param projectCode the code of the project samples should be returned for
         * @param status the status of the samples that should be returned
         * @param sampleCodesToNames list of sample codes with names
         * @since 1.0.0
         */
        @Override
        void samplesWithNames(String projectCode, Status status, Map<String, String> sampleCodesToNames) {
            if (status == Status.SAMPLE_QC_FAIL) {
                List<Sample> samples = parseSamples(sampleCodesToNames)
                viewModel.samples.clear()
                viewModel.samples.addAll(samples)
            } else {
                //there is not behaviour defined so do nothing
            }
        }

        private static List<Sample> parseSamples(Map<String, String> codesToNames) {
            List<Sample> samples = codesToNames.entrySet().stream()
                    .map({
                        return new Sample(it.key, it.value)
                    }).collect()
            return Optional.ofNullable(samples).orElse([])
        }
    }
}
