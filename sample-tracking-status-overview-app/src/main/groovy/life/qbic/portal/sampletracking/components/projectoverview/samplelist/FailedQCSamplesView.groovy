package life.qbic.portal.sampletracking.components.projectoverview.samplelist


import com.vaadin.data.provider.DataProvider
import com.vaadin.ui.Grid
import com.vaadin.ui.VerticalLayout
import life.qbic.business.samples.info.GetSamplesInfoOutput
import life.qbic.datamodel.samples.Status

/**
 * <b><short description></b>
 *
 * <p><detailed description></p>
 *
 * @since <version tag>
 */
class FailedQCSamplesView extends VerticalLayout {
    private final ViewModel viewModel
    private final Presenter presenter

    private Grid<Sample> samplesGrid

    FailedQCSamplesView() {
        this.viewModel = new ViewModel()
        this.presenter = new Presenter(viewModel)
        initLayout()
    }

    private void initLayout() {
        this.setMargin(false)
        samplesGrid = createSamplesGrid()
        this.addComponent(samplesGrid)
    }

    private Grid<Sample> createSamplesGrid() {
        //TODO implement
        Grid<Sample> samplesGrid = new Grid<>()
        samplesGrid.setCaption("Samples that failed quality control")
        samplesGrid.addColumn(Sample::getCode).setCaption("Sample Code").setId("SampleCode")
        samplesGrid.addColumn(Sample::getTitle).setCaption("Sample Title").setId("SampleTitle")
        samplesGrid.setSelectionMode(Grid.SelectionMode.NONE)
        samplesGrid.setDataProvider(DataProvider.ofCollection(viewModel.getSamples()))
        samplesGrid
    }

    public GetSamplesInfoOutput getPresenter() {
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

        Presenter(ViewModel viewModel) {
            this.viewModel = viewModel
        }

        @Override
        void failedExecution(String reason) {

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
