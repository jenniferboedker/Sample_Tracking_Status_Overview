package life.qbic.portal.sampletracking.components.projects;

import static org.apache.logging.log4j.LogManager.getLogger;

import com.vaadin.event.selection.SingleSelectionEvent;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
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

  private final SampleStatusSummaryProvider sampleStatusSummaryProvider;

  protected ResponsiveGrid<Project> projectGrid;

  private final ProjectRepository projectRepository;

  private HorizontalLayout spinnerLayout;

  private final List<ProjectSelectionListener> projectSelectionListeners = new ArrayList<>();


  public ProjectView(ExecutorService projectLoadingExecutor,
      SampleStatusSummaryProvider sampleStatusSummaryProvider,
      ProjectRepository projectRepository) {
    this.sampleStatusSummaryProvider = sampleStatusSummaryProvider;
    this.projectLoadingExecutor = projectLoadingExecutor;
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
    if (project.hasAvailableData()) {
      showDownloadButton();
    }
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
            projectGrid.setItems(projects);
          });
        });

  }

  private void avoidElementOverlap() {
    this.addStyleName("responsive-grid-layout");
    this.setWidthFull();
  }

  private ResponsiveGrid<Project> createProjectGrid() {
    ResponsiveGrid<Project> grid = new ResponsiveGrid<>();
    grid.addColumn(it -> {
          if (Objects.isNull(it.subscriptionStatus())) {
            return false;
          }
          return it.subscriptionStatus().isSubscribed();
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
            it -> sampleStatusSummaryProvider.getForProject(it.code()))
        .setId("projectStatus")
        .setHandleWidgetEvents(true)
        .setMinimumWidth(900)
        .setSortable(false);

    grid.addColumn(it -> {
      if (Objects.isNull(it.projectStatus())) {
        return Instant.MIN;
      }
      return it.projectStatus().getLastModified();
    }).setId("lastModified").setHidden(true);

    grid.setSizeFull();
    grid.setSelectionMode(SelectionMode.SINGLE);
    return grid;
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
