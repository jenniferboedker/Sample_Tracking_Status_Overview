package life.qbic.portal.sampletracking.components.sampleoverview

import com.vaadin.data.provider.DataProvider
import com.vaadin.data.provider.ListDataProvider
import com.vaadin.ui.ComboBox
import life.qbic.business.samples.Sample
import life.qbic.business.samples.info.GetSamplesInfoOutput
import life.qbic.datamodel.samples.Status
import life.qbic.portal.sampletracking.communication.notification.NotificationService
import life.qbic.portal.sampletracking.components.ViewModel
import life.qbic.portal.sampletracking.components.projectoverview.statusdisplay.State

class SampleView extends SampleDesign {

  private final ViewModel viewModel
  private final Presenter presenter

  private final SampleFilter sampleFilter = new SampleFilterImpl()


  SampleView(ViewModel viewModel, NotificationService notificationService) {
    super()
    this.viewModel = viewModel
    this.presenter = new Presenter(notificationService, viewModel)


    activateViewToggle()
    createSamplesGrid()
    enableUserSampleFiltering()
    addColumnColoring()
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
    ListDataProvider<Sample> dataProvider = ListDataProvider.ofCollection(viewModel.samples)
    dataProvider.addFilter((Sample it) -> sampleFilter.asPredicate().test(it))
    sampleGrid.setDataProvider(dataProvider)
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
    ComboBox<Status> statusComboBox = this.shownStatus
    DataProvider<Sample, ?> dataProvider = this.sampleGrid.getDataProvider()
    statusComboBox.setItems(
            Status.METADATA_REGISTERED,
            Status.SAMPLE_RECEIVED,
            Status.SAMPLE_QC_FAIL,
            Status.SAMPLE_QC_PASS,
            Status.LIBRARY_PREP_FINISHED,
            Status.DATA_AVAILABLE
    )
    statusComboBox.setItemCaptionGenerator({ it.getDisplayName() })
    statusComboBox.setEmptySelectionCaption("All statuses")
    statusComboBox.addValueChangeListener({
      if (it.getValue()) {
        sampleFilter.withStatus(it.getValue().toString())
      } else {
        sampleFilter.clearStatus()
      }
      dataProvider.refreshAll()
    })
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
