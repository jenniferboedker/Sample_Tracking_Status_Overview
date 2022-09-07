package life.qbic.portal.sampletracking.view.projects;

import com.vaadin.ui.Component;
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

//    receivedLayout.setComponentAlignment(receivedCountLabel, Alignment.TOP_CENTER);
//    sampleQcLayout.setComponentAlignment(sampleQcCountLabel, Alignment.TOP_CENTER);
//    libraryPreparedLayout.setComponentAlignment(libraryPreparedCountLabel, Alignment.TOP_CENTER);
//    dataAvailableLayout.setComponentAlignment(dataAvailableCountLabel, Alignment.TOP_CENTER);

    receivedLayout.setMargin(false); //determined by css
    sampleQcLayout.setMargin(false); //determined by css
    libraryPreparedLayout.setMargin(false); //determined by css
    dataAvailableLayout.setMargin(false); //determined by css

    receivedLayout.addStyleName("status-cell");
    sampleQcLayout.addStyleName("status-cell");
    libraryPreparedLayout.addStyleName("status-cell");
    dataAvailableLayout.addStyleName("status-cell");

    receivedLayout.setWidthUndefined();
    sampleQcLayout.setWidthUndefined();
    libraryPreparedLayout.setWidthUndefined();
    dataAvailableLayout.setWidthUndefined();

    statusLayout = new HorizontalLayout();
    statusLayout.setMargin(false);
    statusLayout.setSpacing(false);
    statusLayout.addComponents(receivedLayout,
        sampleQcLayout,
        libraryPreparedLayout,
        dataAvailableLayout);

    statusLayout.setWidthUndefined();

    errorMessage = new Label("Information not available");

    HorizontalLayout panelContent = new HorizontalLayout();
    panelContent.setMargin(false); // determined by CSS
    panelContent.setSpacing(false); // determined by CSS
    panelContent.addComponent(spinner);
//    panelContent.setComponentAlignment(spinner, Alignment.TOP_CENTER);

    panelContent.addComponent(statusLayout);
//    panelContent.setComponentAlignment(statusLayout, Alignment.TOP_CENTER);
    panelContent.addComponent(errorMessage);
//    panelContent.setComponentAlignment(errorMessage, Alignment.TOP_CENTER);

    setCompositionRoot(panelContent);
    panelContent.setWidthUndefined();

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

    removeStateStyles(receivedCountLabel);
    removeStateStyles(sampleQcCountLabel);
    removeStateStyles(libraryPreparedCountLabel);
    removeStateStyles(dataAvailableCountLabel);

    receivedLayout.addStyleName(getStyleName(projectStatus.countReceived(), 0,
        projectStatus.totalCount()));
    sampleQcLayout.addStyleName(getStyleName(projectStatus.countPassedQc(),
        projectStatus.countFailedQc(),
        projectStatus.totalCount()));
    libraryPreparedLayout.addStyleName(getStyleName(projectStatus.countLibraryPrepared(), 0,
        projectStatus.totalCount()));
    dataAvailableLayout.addStyleName(getStyleName(projectStatus.countDataAvailable(), 0,
        projectStatus.totalCount()));

    receivedCountLabel.setSizeUndefined();
    sampleQcCountLabel.setSizeUndefined();
    libraryPreparedCountLabel.setSizeUndefined();
    dataAvailableCountLabel.setSizeUndefined();
    loadedData = projectStatus;
  }

  private void removeStateStyles(Component component) {
    component.removeStyleNames(State.FAILED.getCssClass(), State.IN_PROGRESS.getCssClass(), State.COMPLETED.getCssClass());
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
