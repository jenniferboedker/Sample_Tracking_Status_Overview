package life.qbic.portal.sampletracking.components.projects;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Composite;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import life.qbic.portal.sampletracking.TrackingStatusProvider;
import life.qbic.portal.sampletracking.components.Spinner;
import life.qbic.portal.sampletracking.components.projects.viewmodel.ProjectStatus;

public class SampleStatusSummary extends Composite {

  private final ExecutorService executorService;
  private final TrackingStatusProvider trackingStatusProvider;
  private final Label receivedCountLabel;
  private final Label sampleQcCountLabel;
  private final Label libraryPreparedCountLabel;
  private final Label dataAvailableCountLabel;
  private final String projectCode;
  private final Spinner spinner;
  private final HorizontalLayout statusLayout;
  private Future<?> loadingTask;

  private ProjectStatus loadedData;


  public SampleStatusSummary(String projectCode, ExecutorService executorService,
      TrackingStatusProvider trackingStatusProvider) {
    this.projectCode = projectCode;
    this.executorService = executorService;
    this.trackingStatusProvider = trackingStatusProvider;
    spinner = new Spinner();
    HorizontalLayout panelContent = new HorizontalLayout();
    panelContent.setMargin(false);
    panelContent.addComponent(spinner);
    panelContent.setComponentAlignment(spinner, Alignment.MIDDLE_CENTER);

    receivedCountLabel = new Label();
    sampleQcCountLabel = new Label();
    libraryPreparedCountLabel = new Label();
    dataAvailableCountLabel = new Label();

    HorizontalLayout receivedLayout = new HorizontalLayout( receivedCountLabel);
    HorizontalLayout sampleQcLayout = new HorizontalLayout( sampleQcCountLabel);
    HorizontalLayout libraryPreparedLayout = new HorizontalLayout(
        libraryPreparedCountLabel);
    HorizontalLayout dataAvailableLayout = new HorizontalLayout(
        dataAvailableCountLabel);

    receivedLayout.setComponentAlignment(receivedCountLabel, Alignment.TOP_RIGHT);
    sampleQcLayout.setComponentAlignment(sampleQcCountLabel, Alignment.TOP_RIGHT);
    libraryPreparedLayout.setComponentAlignment(libraryPreparedCountLabel, Alignment.TOP_RIGHT);
    dataAvailableLayout.setComponentAlignment(dataAvailableCountLabel, Alignment.TOP_RIGHT);

    receivedLayout.setMargin(false);
    sampleQcLayout.setMargin(false);
    libraryPreparedLayout.setMargin(false);
    dataAvailableLayout.setMargin(false);

    receivedLayout.setWidth(100, Unit.PIXELS);
    sampleQcLayout.setWidth(100, Unit.PIXELS);
    libraryPreparedLayout.setWidth(100, Unit.PIXELS);
    dataAvailableLayout.setWidth(100, Unit.PIXELS);

    statusLayout = new HorizontalLayout();
    statusLayout.setMargin(false);
    statusLayout.setSpacing(false);
    statusLayout.addComponents(receivedLayout,
        sampleQcLayout,
        libraryPreparedLayout,
        dataAvailableLayout);

    statusLayout.setWidth(400, Unit.PIXELS);
    panelContent.addComponents(statusLayout);
    setCompositionRoot(panelContent);

    panelContent.setWidthFull();

  }

  @Override
  public void attach() {
    super.attach();
    loadInformation();
  }

  private void loadInformation() {
    if (Objects.nonNull(loadedData)) {
      return;
    }
    UI ui = getUI();
    ui.setPollInterval(200);
    statusLayout.setVisible(false);
    spinner.setVisible(true);
    loadingTask = executorService.submit(() -> {
      ProjectStatus loadedStatus = trackingStatusProvider.getForProject(projectCode);
      ui.access(() -> {
        showProjectStatus(loadedStatus);
        spinner.setVisible(false);
        statusLayout.setVisible(true);
        this.setWidthFull();
      });
    });
  }

  private void showProjectStatus(ProjectStatus projectStatus) {
    if (!projectStatus.equals(loadedData)) {
      receivedCountLabel.setValue(String.format("%s / %s",
          projectStatus.countReceived(),
          projectStatus.totalCount()));

      sampleQcCountLabel.setValue(String.format("%s / %s",
          projectStatus.countPassedQc(),
          projectStatus.totalCount()));

      libraryPreparedCountLabel.setValue(String.format("%s / %s",
          projectStatus.countLibraryPrepared(),
          projectStatus.totalCount()));

      dataAvailableCountLabel.setValue(String.format("%s / %s",
          projectStatus.countDataAvailable(),
          projectStatus.totalCount()));

      receivedCountLabel.setStyleName(" .status-completed");
      sampleQcCountLabel.setStyleName(" .status-completed");
      libraryPreparedCountLabel.setStyleName(" .status-completed");
      dataAvailableCountLabel.setStyleName(" .status-completed");

      receivedCountLabel.setSizeUndefined();
      sampleQcCountLabel.setSizeUndefined();
      libraryPreparedCountLabel.setSizeUndefined();
      dataAvailableCountLabel.setSizeUndefined();

      loadedData = projectStatus;
    }
  }

  @Override
  public void detach() {
    if (Objects.nonNull(loadingTask)) {
      loadingTask.cancel(true);
    }
    super.detach();
  }
}
