package life.qbic.portal.sampletracking.components.sampleoverview

import com.vaadin.data.provider.ListDataProvider
import com.vaadin.shared.ui.grid.HeightMode
import com.vaadin.ui.Grid
import life.qbic.business.samples.Sample
import life.qbic.business.samples.info.GetSamplesInfoOutput
import life.qbic.portal.sampletracking.communication.notification.NotificationService
import life.qbic.portal.sampletracking.components.ViewModel

class SampleView extends SampleDesign{

    private final ViewModel viewModel
    private final Presenter presenter


    SampleView(ViewModel viewModel, NotificationService notificationService) {
        super()
        this.viewModel = viewModel
        this.presenter = new Presenter(notificationService, viewModel)


        activateViewToggle()
        createSamplesGrid()
    }

    private void activateViewToggle() {
        this.projectsButton.addClickListener({
            viewModel.projectViewEnabled = true
        })

        viewModel.addPropertyChangeListener("projectViewEnabled",{
            if(viewModel.projectViewEnabled){
                this.projectsButton.setEnabled(false)
            }else{
                this.projectsButton.setEnabled(true)
            }
        })
    }

    private void createSamplesGrid() {
        ListDataProvider<Sample> dataProvider = ListDataProvider.ofCollection(viewModel.samples)
        sampleGrid.setDataProvider(dataProvider)
    }

    void reset(){
        viewModel.samples.clear()
    }

    Presenter getPresenter(){
        return presenter
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
}
