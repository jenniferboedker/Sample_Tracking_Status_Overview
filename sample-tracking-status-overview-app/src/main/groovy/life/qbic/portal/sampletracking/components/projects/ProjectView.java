package life.qbic.portal.sampletracking.components.projects;

import static org.apache.logging.log4j.LogManager.getLogger;

import com.vaadin.event.selection.SingleSelectionEvent;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import life.qbic.portal.sampletracking.SubscriptionStatusProvider;
import life.qbic.portal.sampletracking.components.ResponsiveGrid;
import life.qbic.portal.sampletracking.components.Spinner;
import life.qbic.portal.sampletracking.components.projects.ProjectView.ProjectSelectionListener.ProjectSelectionEvent;
import life.qbic.portal.sampletracking.components.projects.viewmodel.Project;
import org.apache.logging.log4j.Logger;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public class ProjectView extends ProjectDesign {

  private static final Logger log = getLogger(ProjectView.class);

  private static final int MIN_CODE_COLUMN_WIDTH = 100;
  private final static int MAX_CODE_COLUMN_WIDTH = 200;

  private final ExecutorService projectLoadingExecutor;

  private final ProjectStatusComponentProvider projectStatusComponentProvider;

  private final SubscriptionStatusProvider subscriptionStatusProvider;

  protected ResponsiveGrid<Project> projectGrid;

  private final ProjectRepository projectRepository;

  private HorizontalLayout spinnerLayout;

  private final List<ProjectSelectionListener> projectSelectionListeners = new ArrayList<>();


  public ProjectView(ExecutorService projectLoadingExecutor,
      ProjectStatusComponentProvider projectStatusComponentProvider,
      SubscriptionStatusProvider subscriptionStatusProvider, ProjectRepository projectRepository) {
    this.projectStatusComponentProvider = projectStatusComponentProvider;
    this.projectLoadingExecutor = projectLoadingExecutor;
    this.subscriptionStatusProvider = subscriptionStatusProvider;
    this.projectRepository = projectRepository;
    avoidElementOverlap();
    this.projectGrid = createProjectGrid();
    addProjectGrid();
    addSorting();
    addProjectFilter();
    this.spinnerLayout = createSpinner();
    addSpinner();
    hideDownloadButton();
    listenToProjectSelection();
  }

  private void listenToProjectSelection() {
    projectGrid.addSelectionListener(it -> {
      if (it instanceof SingleSelectionEvent) {
        it.getFirstSelectedItem().ifPresent(this::selectProject);
        if (!it.getFirstSelectedItem().isPresent()) {
          clearSelectedProject();
        }
      }
    });
  }

  private void clearSelectedProject() {
    hideDownloadButton();
    fireProjectSelectionEvent(new ProjectSelectionEvent(null));
  }

  private void selectProject(Project project) {
    fireProjectSelectionEvent(new ProjectSelectionEvent(project.code()));
  }

  private void showDownloadButton() {
    projectsButton.setVisible(true);
  }

  private void hideDownloadButton() {
    downloadButton.setVisible(false);
  }

  private void addSpinner() {
    this.addComponent(spinnerLayout);
    spinnerLayout.setVisible(false);
  }

  private void addProjectGrid() {
    this.addComponent(projectGrid);
  }

  private HorizontalLayout createSpinner() {
    Spinner spinner = new Spinner();
    HorizontalLayout layout = new HorizontalLayout();
    layout.addComponent(spinner);
    layout.setWidthFull();
    layout.setComponentAlignment(spinner, Alignment.MIDDLE_CENTER);
    return layout;
  }

  @Override
  public void attach() {
    super.attach();
    loadProjects();
  }

  private void addProjectFilter() {
    searchField.setValueChangeMode(ValueChangeMode.LAZY);
    searchField.addValueChangeListener(
        it -> projectGrid.setFilter(new ProjectFilter().containingText(it.getValue())));
  }

  private void addSorting() {
    sort.setItems("Newest Changes", "Oldest Changes", "Subscribed", "Not Subscribed");

    sort.addValueChangeListener(it -> {
      if (Objects.nonNull(it.getValue())) {
        switch (it.getValue()) {
          case "Subscribed":
            projectGrid.sort("subscription", SortDirection.DESCENDING);
            break;
          case "Not Subscribed":
            projectGrid.sort("subscription", SortDirection.ASCENDING);
            break;
          case "Newest Changes":
            projectGrid.sort("lastModified", SortDirection.DESCENDING);
            break;
          case "Oldest Changes":
            projectGrid.sort("lastModified", SortDirection.ASCENDING);
            break;
          default:
            projectGrid.clearSortOrder();
        }
      } else {
        projectGrid.clearSortOrder();
      }
    });
  }

  private void loadProjects() {
    UI ui = UI.getCurrent();
    ui.setPollInterval(100);
    spinnerLayout.setVisible(true);
    projectGrid.setVisible(false);
    CompletableFuture.runAsync(
        () -> {
          List<Project> projects = projectRepository.findAll();
          log.info(String.format("loaded %s valid projects", projects.size()));
          ui.access(() -> {
            projectGrid.setVisible(true);
            spinnerLayout.setVisible(false);
            loadSubscriptions(projects);
            projectGrid.setItems(projects);
          });
        });

  }

  private void loadSubscriptions(List<Project> projects) {
    projects.forEach(it -> {
      boolean subscriptionStatus = subscriptionStatusProvider.getForProject(it.code());
      it.setSubscribed(subscriptionStatus);
    });
  }

  private void avoidElementOverlap() {
    this.addStyleName("responsive-grid-layout");
    this.setWidthFull();
  }

  private ResponsiveGrid<Project> createProjectGrid() {
    ResponsiveGrid<Project> grid = new ResponsiveGrid<>();
    grid.addComponentColumn(it -> {
          CheckBox checkBox = new CheckBox();
          checkBox.setValue(it.subscribed());
          return checkBox;
        })
        .setCaption("Subscribe")
        .setId("subscription")
        .setMinimumWidthFromContent(false)
        .setSortable(false)
        .setMinimumWidth(MIN_CODE_COLUMN_WIDTH)
        .setMaximumWidth(MAX_CODE_COLUMN_WIDTH);

    grid.addColumn(Project::title)
        .setCaption("Project Title")
        .setId("title")
        .setMinimumWidthFromContent(false)
        .setExpandRatio(1)
        .setSortable(false);

    grid.addColumn(Project::code)
        .setCaption("Project Code")
        .setId("code")
        .setMinimumWidthFromContent(false)
        .setSortable(false)
        .setMinimumWidth(MIN_CODE_COLUMN_WIDTH)
        .setMaximumWidth(MAX_CODE_COLUMN_WIDTH);

    grid.addComponentColumn(
            it -> projectStatusComponentProvider.getForProject(it.code()))
        .setId("projectStatus")
        .setHandleWidgetEvents(true)
        .setMinimumWidth(4 * ProjectStatusComponent.COLUMN_WIDTH)
        .setSortable(false);

    grid.setSizeFull();
    grid.getHeaderRow(0).getCell("projectStatus").setComponent(getProjectStatusHeader());
    grid.setSelectionMode(SelectionMode.SINGLE);
    return grid;
  }

  private static HorizontalLayout getProjectStatusHeader() {
    HorizontalLayout layout = new HorizontalLayout();
    layout.setMargin(false);
    layout.setSpacing(false);

    Label samplesReceived = new Label("Samples Received");
    Label samplesPassedQc = new Label("Samples Passed QC");
    Label libraryPrepFinished = new Label("Library Prep Finished");
    Label dataAvailable = new Label("Data Available");

    HorizontalLayout samplesReceivedLayout = new HorizontalLayout(samplesReceived);
    HorizontalLayout samplesPassedQcLayout = new HorizontalLayout(samplesPassedQc);
    HorizontalLayout libraryPrepFinishedLayout = new HorizontalLayout(libraryPrepFinished);
    HorizontalLayout dataAvailableLayout = new HorizontalLayout(dataAvailable);

    samplesReceivedLayout.setComponentAlignment(samplesReceived, Alignment.MIDDLE_CENTER);
    samplesPassedQcLayout.setComponentAlignment(samplesPassedQc, Alignment.MIDDLE_CENTER);
    libraryPrepFinishedLayout.setComponentAlignment(libraryPrepFinished, Alignment.MIDDLE_CENTER);
    dataAvailableLayout.setComponentAlignment(dataAvailable, Alignment.MIDDLE_CENTER);

    samplesReceivedLayout.setMargin(false);
    samplesPassedQcLayout.setMargin(false);
    libraryPrepFinishedLayout.setMargin(false);
    dataAvailableLayout.setMargin(false);

    samplesReceivedLayout.setSpacing(false);
    samplesPassedQcLayout.setSpacing(false);
    libraryPrepFinishedLayout.setSpacing(false);
    dataAvailableLayout.setSpacing(false);

    samplesReceivedLayout.setSizeUndefined();
    samplesPassedQcLayout.setSizeUndefined();
    libraryPrepFinishedLayout.setSizeUndefined();
    dataAvailableLayout.setSizeUndefined();

    samplesReceived.setWidth(ProjectStatusComponent.COLUMN_WIDTH, Unit.PIXELS);
    samplesPassedQc.setWidth(ProjectStatusComponent.COLUMN_WIDTH, Unit.PIXELS);
    libraryPrepFinished.setWidth(ProjectStatusComponent.COLUMN_WIDTH, Unit.PIXELS);
    dataAvailable.setWidth(ProjectStatusComponent.COLUMN_WIDTH, Unit.PIXELS);

    layout.addComponents(samplesReceivedLayout, samplesPassedQcLayout, libraryPrepFinishedLayout, dataAvailableLayout);
    return layout;
  }

  public void addProjectSelectionListener(ProjectSelectionListener listener) {
    if (projectSelectionListeners.contains(listener)) {
      return;
    }
    projectSelectionListeners.add(listener);
  }

  public void removeProjectSelectionListener(ProjectSelectionListener listener) {
    projectSelectionListeners.remove(listener);
  }

  private void fireProjectSelectionEvent(ProjectSelectionEvent projectSelectionEvent) {
    projectSelectionListeners.forEach(it -> it.onProjectSelected(projectSelectionEvent));
  }

  public interface ProjectSelectionListener {

    class ProjectSelectionEvent {

      private final String projectCode;

      public ProjectSelectionEvent(String projectCode) {
        this.projectCode = projectCode;
      }

      public Optional<String> projectCode() {
        return Optional.ofNullable(projectCode);
      }
    }

    void onProjectSelected(ProjectSelectionEvent event);
  }


}
