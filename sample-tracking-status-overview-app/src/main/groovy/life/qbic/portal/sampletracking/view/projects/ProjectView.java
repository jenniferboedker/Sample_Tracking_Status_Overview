package life.qbic.portal.sampletracking.view.projects;

import com.vaadin.event.selection.SingleSelectionEvent;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ClientConnector;
import com.vaadin.server.Extension;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.components.grid.HeaderRow;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import life.qbic.portal.sampletracking.data.DownloadManifestProvider;
import life.qbic.portal.sampletracking.data.ProjectRepository;
import life.qbic.portal.sampletracking.data.Subscription;
import life.qbic.portal.sampletracking.data.SubscriptionRepository;
import life.qbic.portal.sampletracking.view.ResponsiveGrid;
import life.qbic.portal.sampletracking.view.Spinner;
import life.qbic.portal.sampletracking.view.projects.ProjectView.SampleViewRequestedListener.SampleViewRequested;
import life.qbic.portal.sampletracking.view.projects.viewmodel.Project;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public class ProjectView extends ProjectDesign {

  private static final Logger log = LogManager.getLogger(ProjectView.class);

  private static final int MIN_CODE_COLUMN_WIDTH = 100;
  private final static int MAX_CODE_COLUMN_WIDTH = 200;

  private final SubscriptionRepository subscriptionRepository;
  private final ProjectStatusComponentProvider projectStatusComponentProvider;
  private final SubscriptionCheckboxProvider subscriptionCheckboxProvider;
  protected ResponsiveGrid<Project> projectGrid;

  private final ProjectRepository projectRepository;

  private final HorizontalLayout spinnerLayout;

  private final List<SampleViewRequestedListener> sampleViewRequestedListeners = new ArrayList<>();
  private final DownloadManifestProvider manifestProvider;


  public ProjectView(ProjectRepository projectRepository,
      SubscriptionRepository subscriptionRepository,
      ProjectStatusComponentProvider projectStatusComponentProvider,
      SubscriptionCheckboxProvider subscriptionCheckboxProvider,
      DownloadManifestProvider manifestProvider) {
    this.projectStatusComponentProvider = projectStatusComponentProvider;
    this.subscriptionRepository = subscriptionRepository;
    this.subscriptionCheckboxProvider = subscriptionCheckboxProvider;
    this.projectRepository = projectRepository;
    this.manifestProvider = manifestProvider;
    avoidElementOverlap();
    this.projectGrid = createProjectGrid();
    addProjectGrid();
    addSorting();
    addProjectFilter();
    this.spinnerLayout = createSpinner();
    addSpinner();
    listenToProjectSelection();
    addTooltips();
    listenToSampleViewButton();
    setupDownloadButton();
  }

  private void setupDownloadButton() {
    downloadButton.setIcon(VaadinIcons.DOWNLOAD);
    hideDownloadButton();
    downloadButton.setDescription(
        "A manifest is a text file with sample codes used by our client application to download the data attached to the defined samples. <br>"
            + "Use <a href=\"https://github.com/qbicsoftware/postman-cli\" target=\"_blank\">"
            + VaadinIcons.EXTERNAL_LINK.getHtml() + " qpostman</a> to download the sample data.",
        ContentMode.HTML);
  }

  private void updateDownloadableProject(String projectCode) {
    removeFileDownloaders(downloadButton);
    FileDownloader fileDownloader = new FileDownloader(
        new StreamResource(() -> manifestProvider.getManifestForProject(projectCode),
            String.format("%s-manifest.txt", projectCode)));
    fileDownloader.extend(downloadButton);
  }

  private static void removeFileDownloaders(ClientConnector clientConnector) {
    List<Extension> fileDownloaders = clientConnector.getExtensions().stream()
        .filter((extension) -> extension instanceof FileDownloader)
        .collect(Collectors.toList());
    fileDownloaders.forEach(clientConnector::removeExtension);
  }

  private void listenToSampleViewButton() {
    this.samplesButton.addClickListener(
        it -> projectGrid.getSelectedItems().stream().findFirst().ifPresent(
            project -> fireSampleViewRequestedListener(new SampleViewRequested(project.code()))));
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
    disableDownloadButton();
    removeFileDownloaders(downloadButton);
    samplesButton.setEnabled(false);
  }

  private void disableDownloadButton() {
    downloadButton.setEnabled(false);
  }

  private void selectProject(Project project) {
    if (project.projectStatus().countDataAvailable() > 0) {
      showDownloadButton();
      enableDownloadButton();
    }
    updateDownloadableProject(project.code());
    if (project.projectStatus().totalCount() < 1) {
      return;
    }
    samplesButton.setEnabled(true);
  }

  private void enableDownloadButton() {
    downloadButton.setEnabled(true);
  }
  private void showDownloadButton() {
    downloadButton.setVisible(true);
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
          List<Project> projects = projectRepository.findAllProjects();
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
    List<Subscription> subscriptions = subscriptionRepository.findAll();
    List<Project> subscribedProjects = projects.stream()
        .filter(project -> subscriptions.stream()
            .anyMatch(subscription -> subscription.projectCode().equals(project.code())))
        .collect(Collectors.toList());
    projects.forEach(it -> it.setSubscribed(subscribedProjects.contains(it)));
  }

  private void avoidElementOverlap() {
    this.addStyleName("responsive-grid-layout");
    this.setWidthFull();
  }

  private ResponsiveGrid<Project> createProjectGrid() {
    ResponsiveGrid<Project> grid = new ResponsiveGrid<>();
    grid.addComponentColumn(subscriptionCheckboxProvider::getForProject)
        .setComparator((p1, p2) -> Boolean.compare(p1.subscribed(), p2.subscribed()))
        .setCaption("Subscribe")
        .setId("subscription")
        .setStyleGenerator(it -> "subscription-cell")
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

    grid.addComponentColumn(projectStatusComponentProvider::getForProject)
        .setId("projectStatus")
        .setHandleWidgetEvents(true)
        .setMinimumWidth(4 * ProjectStatusComponent.COLUMN_WIDTH)
        .setSortable(false);

    grid.addColumn(it -> it.projectStatus().getLastModified())
        .setComparator((p1, p2) -> p1.projectStatus().getLastModified()
            .compareTo(p2.projectStatus().getLastModified()))
        .setHidden(true)
        .setId("lastModified");

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

    samplesReceivedLayout.setDescription("Number of samples that arrived in the sequencing facility.");
    samplesPassedQcLayout.setDescription("Number of samples that passed quality control.");
    libraryPrepFinishedLayout.setDescription("Number of samples where library preparation has finished.");
    dataAvailableLayout.setDescription("Number of available raw datasets.");

    samplesReceivedLayout.addStyleName("header-with-tooltip");
    samplesPassedQcLayout.addStyleName("header-with-tooltip");
    libraryPrepFinishedLayout.addStyleName("header-with-tooltip");
    dataAvailableLayout.addStyleName("header-with-tooltip");

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

    layout.addComponents(samplesReceivedLayout, samplesPassedQcLayout, libraryPrepFinishedLayout,
        dataAvailableLayout);
    return layout;
  }

  private void addTooltips() {
    HeaderRow headerRow = projectGrid.getDefaultHeaderRow();
    headerRow.getCell("subscription")
        .setDescription("Select a project to get send status updates over email.");
  }

  public void addSampleViewRequestedListener(SampleViewRequestedListener listener) {
    if (sampleViewRequestedListeners.contains(listener)) {
      return;
    }
    sampleViewRequestedListeners.add(listener);
  }

  public void removeSampleViewRequestedListener(SampleViewRequestedListener listener) {
    sampleViewRequestedListeners.remove(listener);
  }

  private void fireSampleViewRequestedListener(SampleViewRequested event) {
    sampleViewRequestedListeners.forEach(it -> it.onSampleViewRequested(event));
  }

  @FunctionalInterface
  public interface SampleViewRequestedListener {

    class SampleViewRequested {

      private final String projectCode;

      public SampleViewRequested(String projectCode) {
        this.projectCode = projectCode;
      }

      public String projectCode() {
        return projectCode;
      }

      @Override
      public String toString() {
        return new StringJoiner(", ", SampleViewRequested.class.getSimpleName() + "[", "]")
            .add("projectCode='" + projectCode + "'")
            .toString();
      }
    }

    void onSampleViewRequested(SampleViewRequested event);

  }


}
