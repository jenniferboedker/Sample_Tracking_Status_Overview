package life.qbic.portal.sampletracking.view.projects;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Composite;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import life.qbic.portal.sampletracking.data.ProjectStatusProvider;
import life.qbic.portal.sampletracking.view.Spinner;
import life.qbic.portal.sampletracking.view.projects.viewmodel.Project;
import life.qbic.portal.sampletracking.view.projects.viewmodel.ProjectStatus;

public class ProjectStatusComponent extends Composite {

  public static final int COLUMN_WIDTH = 100;

  private final ExecutorService executorService;
  private final ProjectStatusProvider trackingStatusProvider;
  private final Label receivedCountLabel;
  private final Label sampleQcCountLabel;
  private final Label libraryPreparedCountLabel;
  private final Label dataAvailableCountLabel;

  private final Project project;
  private final Spinner spinner;
  private final HorizontalLayout statusLayout;
  private final HorizontalLayout receivedLayout;
  private final HorizontalLayout sampleQcLayout;
  private final HorizontalLayout libraryPreparedLayout;
  private final HorizontalLayout dataAvailableLayout;
  private final Label errorMessage;
  private Future<?> loadingTask;

  private ProjectStatus loadedData;


  public ProjectStatusComponent(Project project, ExecutorService executorService,
      ProjectStatusProvider trackingStatusProvider) {
    this.project = project;
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

    receivedLayout = new HorizontalLayout(receivedCountLabel);
    sampleQcLayout = new HorizontalLayout(sampleQcCountLabel);
    libraryPreparedLayout = new HorizontalLayout(
        libraryPreparedCountLabel);
    dataAvailableLayout = new HorizontalLayout(
        dataAvailableCountLabel);

    receivedLayout.setComponentAlignment(receivedCountLabel, Alignment.TOP_CENTER);
    sampleQcLayout.setComponentAlignment(sampleQcCountLabel, Alignment.TOP_CENTER);
    libraryPreparedLayout.setComponentAlignment(libraryPreparedCountLabel, Alignment.TOP_CENTER);
    dataAvailableLayout.setComponentAlignment(dataAvailableCountLabel, Alignment.TOP_CENTER);

    receivedLayout.setMargin(false);
    sampleQcLayout.setMargin(false);
    libraryPreparedLayout.setMargin(false);
    dataAvailableLayout.setMargin(false);

    receivedLayout.setWidth(COLUMN_WIDTH, Unit.PIXELS);
    sampleQcLayout.setWidth(COLUMN_WIDTH, Unit.PIXELS);
    libraryPreparedLayout.setWidth(COLUMN_WIDTH, Unit.PIXELS);
    dataAvailableLayout.setWidth(COLUMN_WIDTH, Unit.PIXELS);

    statusLayout = new HorizontalLayout();
    statusLayout.setMargin(false);
    statusLayout.setSpacing(true);
    statusLayout.addComponents(receivedLayout,
        sampleQcLayout,
        libraryPreparedLayout,
        dataAvailableLayout);

    statusLayout.setWidth(4 * COLUMN_WIDTH, Unit.PIXELS);
    panelContent.addComponents(statusLayout);
    setCompositionRoot(panelContent);
    errorMessage = new Label("Information not available");
    panelContent.addComponent(errorMessage);
    panelContent.setComponentAlignment(errorMessage, Alignment.TOP_CENTER);

    panelContent.setWidthFull();

  }

  @Override
  public void attach() {
    super.attach();
    loadInformation();
  }

  private void loadInformation() {
    ProjectStatus projectStatus = project.projectStatus();
    if (Objects.nonNull(loadedData) && Objects.nonNull(projectStatus)) {
      if (loadedData.equals(projectStatus)) {
        return;
      }
    }
    UI ui = getUI();
    ui.setPollInterval(200);
    statusLayout.setVisible(false);
    errorMessage.setVisible(false);
    spinner.setVisible(true);
    loadingTask = executorService.submit(() -> {
      Optional<ProjectStatus> retrieved = trackingStatusProvider.getForProject(project.code());
      ui.access(() -> {
        retrieved.ifPresent(it -> {
          showProjectStatus(it);
          project.setProjectStatus(it);
          statusLayout.setVisible(true);
        });
        if (!retrieved.isPresent()) {
          errorMessage.setVisible(true);
        }
        spinner.setVisible(false);
        this.setWidthFull();
      });
    });
  }

  private void showProjectStatus(ProjectStatus projectStatus) {
    if (projectStatus.equals(loadedData)) {
      return;
    }
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

    receivedLayout.setStyleName(getStyleName(projectStatus.countReceived(), 0,
        projectStatus.totalCount()));
    sampleQcLayout.setStyleName(getStyleName(projectStatus.countPassedQc(),
        projectStatus.countFailedQc(),
        projectStatus.totalCount()));
    libraryPreparedLayout.setStyleName(getStyleName(projectStatus.countLibraryPrepared(), 0,
        projectStatus.totalCount()));
    dataAvailableLayout.setStyleName(getStyleName(projectStatus.countDataAvailable(), 0,
        projectStatus.totalCount()));

    receivedCountLabel.setSizeUndefined();
    sampleQcCountLabel.setSizeUndefined();
    libraryPreparedCountLabel.setSizeUndefined();
    dataAvailableCountLabel.setSizeUndefined();
    loadedData = projectStatus;
  }

  private String getStyleName(int passingCount, int failingCount, int totalCount) {
    State state;
    if (totalCount == 0) {
      state = State.IN_PROGRESS;
    } else if (passingCount == totalCount) {
      state = State.COMPLETED;
    } else if (failingCount > 0) {
      state = State.FAILED;
    } else {
      state = State.IN_PROGRESS;
    }
    return state.getCssClass();
  }

  @Override
  public void detach() {
    if (Objects.nonNull(loadingTask)) {
      loadingTask.cancel(true);
    }
    super.detach();
  }

}
