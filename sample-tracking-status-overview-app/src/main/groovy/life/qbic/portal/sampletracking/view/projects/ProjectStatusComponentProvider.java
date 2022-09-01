package life.qbic.portal.sampletracking.view.projects;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import life.qbic.portal.sampletracking.data.ProjectStatusProvider;
import life.qbic.portal.sampletracking.view.projects.viewmodel.Project;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
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

}
