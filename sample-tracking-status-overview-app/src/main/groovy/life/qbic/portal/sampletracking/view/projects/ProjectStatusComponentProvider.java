package life.qbic.portal.sampletracking.view.projects;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import life.qbic.portal.sampletracking.data.ProjectStatusProvider;
import life.qbic.portal.sampletracking.view.projects.viewmodel.Project;

/**
 * Provides {@link ProjectStatusComponent}s. Caches components to prevent duplicate creation.
 *
 * @since 1.1.4
 */
public class ProjectStatusComponentProvider {
  private final ExecutorService executorService;
  private final ProjectStatusProvider trackingStatusProvider;

  private final Map<String, ProjectStatusComponent> summaries = new HashMap<>();

  public ProjectStatusComponentProvider(ExecutorService executorService,
      ProjectStatusProvider trackingStatusProvider) {
    this.executorService = executorService;
    this.trackingStatusProvider = trackingStatusProvider;
  }

  public ProjectStatusComponent getForProject(Project project) {
    if (summaries.containsKey(project.code())) {
      return summaries.get(project.code());
    }
    ProjectStatusComponent statusSummary = new ProjectStatusComponent(project, executorService,
        trackingStatusProvider);
    summaries.put(project.code(), statusSummary);
    return statusSummary;
  }

  @Override
  protected void finalize() throws Throwable {
    super.finalize();
    executorService.shutdownNow();
  }
}
